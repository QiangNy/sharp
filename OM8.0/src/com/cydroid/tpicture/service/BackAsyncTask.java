package com.cydroid.tpicture.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.cydroid.tpicture.animal.Pig;
import com.cydroid.tpicture.bean.CamDoListenner;
import com.cydroid.tpicture.project.CSW1703;
import com.cydroid.tpicture.utils.BigUtil;
import com.cydroid.tpicture.utils.DswLog;
import com.cydroid.tpicture.utils.Singleton;
import com.cydroid.tpicture.utils.SystemProp;
import com.cydroid.tpicture.utils.TestUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qiang on 4/10/18.
 */
public class BackAsyncTask extends AsyncTask {
    private static final String TAG = "BackAsyncTash";
    private static final String OPEN_CAMERA_ONE = "cy.com.android.mmitest.item.BackCameraTest";
    private static final String OPEN_CAMERA_TWO = "cy.com.android.mmitest.item.BackCameraTest2";
    private static final String OPEN_CAMERA_THR = "cy.com.android.mmitest.item.FrontCameraTest";
    private static final String OPEN_CAMERA_FOU = "cy.com.android.mmitest.item.FrontCameraTest2";

    private static final String BCamera1 = "BackCamera";
    private static final String Fcamera1 = "FrontCamera";


    private Context mContext;
    private int[] cameraIDs;
    private String mPicPath;
    private SharedPreferences mSharedPreferences;
    private boolean isPass = false;
    private boolean isWhite;
    private boolean isFlash;


    public void setmCamDoListenner(CamDoListenner mCamDoListenner) {
        this.mCamDoListenner = mCamDoListenner;
    }

    private CamDoListenner mCamDoListenner;


    public void setCameraIDs(int[] cameraIDs) {
        this.cameraIDs = cameraIDs;
    }

    public BackAsyncTask(Context context) {
        this.mContext = context;

        mSharedPreferences = mContext.getSharedPreferences("mpictureCal",Context.MODE_PRIVATE);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        DswLog.d(TAG, "onCancelled");
        //destroy
        //mCamDoListenner = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mkDicPicture();

        Singleton.getInstance().clearPigList();
    }

    private void mkDicPicture() {
        File dir = new File("/sdcard/DCIM/picture/");
        if (dir.exists() && dir.isDirectory()) {
            DswLog.d(TAG, "exist mkDicPicture");
        } else {
            dir.mkdirs();
            DswLog.d(TAG, "mkDicPicture /sdcard/DCIM/picture/");
        }

        File dir2 = new File("/sdcard/mtklog/picture/");
        if (dir2.exists() && dir2.isDirectory()) {
            DswLog.d(TAG, "exist mkDicPicture2");
          //  File mfile1 = new File("/sdcard/mtklog/picture/result.txt");

        } else {
            dir2.mkdirs();
            DswLog.d(TAG, "mkDicPicture /sdcard/mtklog/picture");
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        DswLog.d(TAG, "onPostExecute");
        mCamDoListenner.onCameraDone(isPass);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        DswLog.d(TAG, "doInBackground");
        //2.open cameraID
        switchOpenCamera();

     //   closeCamera();

        if(isWhite) {
           return null;
        } else {
            judeEveryLines();

            checkAllResult();
        }

        return null;
    }

    private void closeCamera() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        DswLog.d(TAG, "kill camera app");
        Method forceStopPackage = null;
        try {
            forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert forceStopPackage != null;
        forceStopPackage.setAccessible(true);
        try {
            forceStopPackage.invoke(activityManager, "com.android.camera");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private void checkAllResult() {
        List<Pig> mList = Singleton.getInstance().getPigList();
        int rel = 0;
        int curCamId = -1;

        String sn = TestUtil.getProperty(SystemProp.PROP_GSM_SERIAL);
        String sub = null;
        String result_path = null;

        if (sn.length() > 17)
        sub = sn.substring(0, 18);

        for (Pig pig: mList) {
            rel = rel + pig.getSuccess();
            curCamId = pig.getCamerIntId();
            //get sn
            StringBuffer sb = new StringBuffer();
            sb.append(sub).append(";");
            //get picturepatch
            sb.append(pig.getPicPath()).append(";");
            //get camreia
            sb.append(curCamId).append(";");
            //test result
            if (pig.getSuccess() == 0) {
                sb.append("0").append(";");
            }else {
                sb.append("1").append(";");
            }
            //sfr
            sb.append(pig.getDegreetoString()).append("\n");

            if (curCamId == 1) {
                result_path = "/sdcard/mtklog/picture/rear_result.txt";
            }else if (curCamId == 2) {
                result_path = "/sdcard/mtklog/picture/front_result.txt";
            }
            writeReltoFile(sb.toString(), result_path);
            DswLog.d(TAG,result_path + " thie file is OK");
        }

        //save2Sdcard

        isPass = rel == 0 ? true : false;
    }

    private void writeReltoFile(String text, String path) {
    //    File file = new File("/sdcard/mtklog/picture/result.txt");
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(text);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void judeEveryLines() {
        List<Pig> mList = Singleton.getInstance().getPigList();
        int[] cRelexLines = new int[20];

        for (Pig pig : mList) {
            for (int m = 0; m < pig.getDegree().length; m++) {

                if (pig.getCameraID().equals(BCamera1)) {
                    //后置由20个值变为16个
                    //cRelexLines = CSW1703.backRelexLines;
                    cRelexLines = CSW1703.backRelexLines_v2;
                }else {
                    cRelexLines = CSW1703.frontRelexLines;
                }

                if (pig.getDegree()[m] >= cRelexLines[m]) {
                    //OK
                    pig.setSuccess(0);
                }else {
                    //Fail
                    pig.setSuccess(1);
                    pig.setTestResult("the " + m + " line test Fail result=" + pig.getDegree()[m] + " < "+ cRelexLines[m]);
                }
                DswLog.d(TAG,"getSuceess="+pig.getSuccess()+ "m="+m );
            }
        }
    }
  /*  private void judeEveryLine() {
        List<Pig> mList = Singleton.getInstance().getPigList();
        int mRelexLine_15 = 0;
        int mRelexLine_19 = 0;

        for (Pig pig : mList) {
            if (pig.getCameraID().equals(BCamera1)) {
                //back_15 & back_19
                mRelexLine_15 = CSW1703.backRelexLine_15;
                mRelexLine_19 = CSW1703.backRelexLine_19;

            }else {
                //front_15 & front_19
                mRelexLine_15 = CSW1703.frontRelexLine_15;
                mRelexLine_19 = CSW1703.frontRelexLine_19;

            }

            for (int m = 0; m < pig.getDegree().length; m++) {
                if (m < 16 ) {
                    if (pig.getDegree()[m] >= mRelexLine_15) {
                        //OK
                        pig.setSuccess(0);
                    }else {
                        //Fail
                        pig.setSuccess(1);
                        pig.setTestResult("the " + m + " line test Fail result=" + pig.getDegree()[m] + " < "+mRelexLine_15);
                    }

                }else {
                    if (pig.getDegree()[m] >= mRelexLine_19) {
                        //OK
                        pig.setSuccess(0);
                    }else {
                        //Fail
                        pig.setSuccess(1);
                        pig.setTestResult("the " + m + " line test Fail result=" + pig.getDegree()[m] + " < "+mRelexLine_19);
                    }
                }
                DswLog.d(TAG,"getSuceess="+pig.getSuccess()+ "m="+m );
            }

        }

    }*/

    private int calculatePicture(String action,String path) {


        String CameraID = null;
        int CamreaIntId = 0;
        int rel  = -1;
        if(action.equals(OPEN_CAMERA_ONE)) {
            CameraID = BCamera1;
            CamreaIntId = 1;
            rel = com.cydroid.tpicture.SharpnessEngine.setMetricLabels(BigUtil.getrMetricLabels(),16);
        }else {
            CameraID = Fcamera1;
            CamreaIntId = 2;
            rel = com.cydroid.tpicture.SharpnessEngine.setMetricLabels(BigUtil.getfMetricLabels(),20);
        }
        DswLog.d(TAG, "getMetricLabels rel="+rel);
        //choose project

        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Bitmap.Config.ARGB_8888;
            DswLog.d(TAG,"BitmapFactory.decodeFile");
            Bitmap bitmapSrc = BitmapFactory.decodeFile(path, op);
            DswLog.d(TAG,"BitmapFactory.decodeFile ok");
            final int PATTERN_NUMBERS = 20;
            int[] degree = new int[PATTERN_NUMBERS];
            int[] xPos = new int[PATTERN_NUMBERS];
            int[] yPos = new int[PATTERN_NUMBERS];
            DswLog.d(TAG,"start getSharpnessValues");
            int success = com.cydroid.tpicture.SharpnessEngine.getSharpnessValues(bitmapSrc,CSW1703.threshold, degree, xPos, yPos);
            DswLog.d(TAG, action + "\ncalculatePicture success=" +success +" CameraId="+CameraID);

            Singleton.getInstance().getPigList().add(new Pig(CameraID,CamreaIntId,success,path,degree));
            return success;
        }catch (Exception e) {
            DswLog.d(TAG, e.getMessage());
        }
        return -1;
    }

    private void switchOpenCamera() {
        for(int j : cameraIDs) {

           // int num = (int) ((Math.random() * 9 + 1) * 10000);

            if (j == 1) {
                mPicPath = "/sdcard/DCIM/picture/rear_camera_picture.jpg";
                openCamera(OPEN_CAMERA_ONE,mPicPath);
            }else if (j == 2) {
                mPicPath = "/sdcard/DCIM/picture/second_rear_camera_picture.jpg";
                openCamera(OPEN_CAMERA_TWO,mPicPath);
            }else if (j == 3) {
                mPicPath = "/sdcard/DCIM/picture/front_camera_picture.jpg";
                openCamera(OPEN_CAMERA_THR,mPicPath);
            }else if (j == 4) {
                mPicPath = "/sdcard/DCIM/picture/second_front_camera_picture.jpg";
                openCamera(OPEN_CAMERA_FOU,mPicPath);
        }

            DswLog.d(TAG, "item j="+j + " mPicPath="+mPicPath);
        }

    }

    private synchronized void openCamera(String action, String Path) {

        File mFile = new File(Path);
        if (mFile.exists())
            mFile.delete();
        DswLog.d(TAG, "sendintent " + action);
        Intent intent = new Intent(action);
        DswLog.d(TAG, "#### 1");
        intent.putExtra("rePicPath", Path);
        intent.putExtra("istPicTest",!isFlash);
        mContext.startActivity(intent);

        //take picture

        DswLog.d(TAG, "#### 2");
        if (action.equals(OPEN_CAMERA_ONE) || action.equals(OPEN_CAMERA_TWO)) {
            SystemClock.sleep(3500);
        }else {
            SystemClock.sleep(2500);
        }

        excuteEventAciton(KeyEvent.KEYCODE_CAMERA);
        SystemClock.sleep(1500);
        DswLog.d(TAG, "#### 3");
        while (!mFile.exists()) {
            SystemClock.sleep(100);
            DswLog.d(TAG, "mFile is not exist");
        }
        SystemClock.sleep(100);
        DswLog.d(TAG, "isWhite="+isWhite);
        if (!isWhite)
            calculatePicture(action,Path);
        DswLog.d(TAG, "#### 5");
    }

    private void excuteEventAciton(int keyevent) {
        try {
            String keyCommand = "input keyevent " + keyevent;
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(keyCommand);
        }catch (IOException e) {
            DswLog.d(TAG, "excuteEventAciton IOException keyEvent="+keyevent);
            e.printStackTrace();
        }
    }

    public void setWhite(boolean white) {
        this.isWhite = white;
    }

    public void setFlash(boolean flash) {
        this.isFlash = flash;
    }
}
