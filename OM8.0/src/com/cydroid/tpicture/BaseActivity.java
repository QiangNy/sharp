package com.cydroid.tpicture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import com.cydroid.tpicture.service.PictureService;
import com.cydroid.tpicture.utils.BigUtil;
import com.cydroid.tpicture.utils.DswLog;
import android.app.ActivityManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by qiang on 4/12/18.
 */
public class BaseActivity  extends Activity {
    private static final String TAG = BaseActivity.class.getName();


    private BroadcastReceiver mBroasCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DswLog.d(TAG, "action="+intent.getAction());
            if (intent.getAction().equals(BigUtil.BROAD_START_TEST)) {
                getIntentAciton(intent);
            }else if (intent.getAction().equals(BigUtil.BROAD_STOP_TEST)) {
                closeCamera();
                finish();
            } /*else if (intent.getAction().equals(BigUtil.BROAD_STAT_WHITETEST)) {
                getIntentAciton(intent);
            }*/
        }
    };

    private void closeCamera() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
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

    @Override
    protected void onStart() {
        super.onStart();

        //jie suo
        sendBroadcast(new Intent("com.chenyee.action.DISABLE_KEYGUARD"));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter =  new IntentFilter();
        intentFilter.addAction(BigUtil.BROAD_START_TEST);
        intentFilter.addAction(BigUtil.BROAD_STOP_TEST);
        registerReceiver(mBroasCast,intentFilter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroasCast);
    }

    private void getIntentAciton(Intent intent) {
        //start camera
        int CameraId = intent.getIntExtra("mCameraID",-1);
        DswLog.d(TAG, "start test mCameraID="+CameraId);
        boolean white = intent.getBooleanExtra("isWhite", false);
        boolean flash = intent.getBooleanExtra("isFlash", false);
        if (CameraId != -1) {
            startPictureService(CameraId, white, flash);
        }
    }


    private void startPictureService(int action, boolean isWhite, boolean isFlash) {
        Intent intent = new Intent(this.getApplicationContext(),
                PictureService.class);
        intent.putExtra(BigUtil.ACTION_ASK_PICTURESERVICE, action);
        intent.putExtra("isWhite", isWhite);
        intent.putExtra("isFlash",isFlash);
        startService(intent);
    }
}
