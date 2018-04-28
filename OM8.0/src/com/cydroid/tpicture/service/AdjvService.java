package com.cydroid.tpicture.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.cydroid.tpicture.R;
import com.cydroid.tpicture.utils.DswLog;

public class AdjvService extends Service {

    private static final String TAG = "AdjvService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationChannel channel = new NotificationChannel("id","name", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);


        Notification notification = new Notification.Builder(AdjvService.this,"id").setSmallIcon(R.drawable.arrow).build();

        startForeground(1, notification);
        DswLog.e(TAG, "start AdjvService notification");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        DswLog.e(TAG, "stop AdjvService");
        super.onDestroy();
    }

}
