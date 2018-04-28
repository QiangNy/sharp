package com.cydroid.tpicture.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.cydroid.tpicture.TestResult;
import com.cydroid.tpicture.bean.CamDoListenner;
import com.cydroid.tpicture.project.CSW1703;
import com.cydroid.tpicture.utils.BigUtil;
import com.cydroid.tpicture.utils.DswLog;
import com.cydroid.tpicture.utils.Singleton;

import cy.com.android.mmitest.service.INvRamService;

/**
 * Created by qiang on 4/9/18.
 */
public class PictureService extends Service implements CamDoListenner{
    private static final String TAG = "PictureService";
    private int Count = 29;
    private int taskID = -1;

    private BackAsyncTask mBackAsyncTask;
    private final int ACTION_START_TEST = 1;
    private INvRamService mService;
    private RemoteServiceConnection mConnection;
    private boolean rel;

    private class RemoteServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder service) {

            mService = INvRamService.Stub.asInterface(service);
            if (null == mService) {
                DswLog.e(TAG, "Error: INvRamService null");
            } else {
                DswLog.e(TAG, "Connected: INvRamService ");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            DswLog.e(TAG, "INvRamService Disconnected");
            mService = null;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            startAction(intent);
        } else {
            DswLog.d(TAG, "onStartCommand intent is NULL");
        }
        return START_STICKY;
    }

    private void startAction(Intent intent) {
        mBackAsyncTask = new BackAsyncTask(this.getApplicationContext());
        mBackAsyncTask.setmCamDoListenner(this);

        taskID = intent.getIntExtra(BigUtil.ACTION_ASK_PICTURESERVICE, BigUtil.ACTION_DEFAULT_ERROR);
        boolean iswhite = intent.getBooleanExtra("isWhite", false);
        boolean isFlash = intent.getBooleanExtra("isFlash", false);
        if (taskID == 100) {
            //Switch 1703
            mBackAsyncTask.setCameraIDs(CSW1703.mCameraIDs);
        }else {
            //open single camera
            mBackAsyncTask.setCameraIDs(new int[]{taskID});
        }
        mBackAsyncTask.setWhite(iswhite);
        mBackAsyncTask.setFlash(isFlash);

        mBackAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        startMMIAService();
    }

    private void startMMIAService() {
        Intent intentAService = new Intent(BigUtil.ACTION_START_MMIASERVICE);
        mConnection = new RemoteServiceConnection();
        intentAService.setComponent(new ComponentName("cy.com.android.mmitest", "cy.com.android.mmitest.service.NvRamService"));
        bindService(intentAService, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DswLog.d(TAG,"MMIAService onDestroy");
        if (mConnection != null)
            unbindService(mConnection);
        mService = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            DswLog.i(TAG, " mHandler msg.what=" + msg.what);
            switch (msg.what) {
                case BigUtil.ACTION_WRITE_NV_FLAG:

                    boolean rel = false;
                    if (mService == null) {
                        Toast.makeText(PictureService.this, "MMI mService is null", Toast.LENGTH_LONG).show();
                    }else {
                        //write nvflag
                        writeNvData(BigUtil.SN_LONG_LENGTH, -1);

                        //ReadNvdata to update Current flag
                        rel = isFlagPass(BigUtil.SN_LONG_LENGTH);
                    }
                    String result = rel ? "Pass": "Fail";
                    Toast.makeText(PictureService.this, "Test Result=" + result, Toast.LENGTH_LONG).show();
                    /*Intent intent = new Intent(PictureService.this, TestResult.class);
                    intent.putExtra("nvram_flag_501", rel);
                    startActivity(intent);
                    Singleton.getInstance().getPictureListenner().onPicture(rel);*/
                    break;
                default:
                    DswLog.d(TAG, "do not have this action="+msg.what);
                    break;
            }
        }
    };

    @Override
    public void onCameraDone(boolean rel) {
        mBackAsyncTask.cancel(true);
        mBackAsyncTask = null;

        this.rel = rel;
        mHandler.sendMessage(mHandler.obtainMessage(BigUtil.ACTION_WRITE_NV_FLAG));
    }

    private boolean isFlagPass(int sn_length) {
        byte[] mSnByteArray = new byte[sn_length];
        try {
            System.arraycopy(mService.readINvramInfo(sn_length), 0, mSnByteArray, 0, sn_length);
            String snNumber = new String(mSnByteArray);
            if (snNumber == null || snNumber.isEmpty()) {
                DswLog.v(TAG, "isUpdateNvData oldSn is null or empty!");
                Toast.makeText(PictureService.this, "Error: SN is null", Toast.LENGTH_LONG).show();
            }
            if (mSnByteArray != null && mSnByteArray.length > BigUtil.ACTION_NVRAM_FLAG_501) {
                DswLog.d(TAG, "isFlagPass:mSnByteArray[501]=" + mSnByteArray[BigUtil.ACTION_NVRAM_FLAG_501]);
                if (mSnByteArray[BigUtil.ACTION_NVRAM_FLAG_501] == BigUtil.NVRAM_FLAG_PASS) {
                    return true;
                }

            }

        } catch (Exception e) {
            DswLog.e(TAG, "isUpdateNvData oldSn Exception:" + e.getMessage());
            Toast.makeText(PictureService.this, "Error: SN is null", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void writeNvData(int sn_length,int action) {
        byte[] mSnByteArray = new byte[sn_length];
        //ReadNvdata
        try {
            System.arraycopy(mService.readINvramInfo(sn_length), 0, mSnByteArray, 0, sn_length);
            String snNumber = new String(mSnByteArray);
            if (snNumber == null || snNumber.isEmpty()) {
                DswLog.v(TAG, "writeNvData oldSn is null or empty!");
                Toast.makeText(PictureService.this, "Error: SN is null", Toast.LENGTH_LONG).show();
                return;
            }
            //replace flag
            mSnByteArray = wFactoryNvData(mSnByteArray);

            //WriteNvdata
            mService.writeToNvramInfo(mSnByteArray, sn_length);

        } catch (Exception e) {
            e.printStackTrace();
            DswLog.e(TAG, "writeNvData oldSn Exception:" + e.getMessage());
        }
    }

    private static byte[] getNewSN(int position, String value, byte[] sn) {

        sn[position] = value.getBytes()[0];
        return sn;
    }

    private byte[] wFactoryNvData(byte[] mSnByteArray) {

        return getNewSN(BigUtil.ACTION_NVRAM_FLAG_501, rel ? "P" : "F", mSnByteArray);
    }

}
