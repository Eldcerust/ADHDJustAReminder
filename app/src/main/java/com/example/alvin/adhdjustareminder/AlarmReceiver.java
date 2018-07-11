package com.example.alvin.adhdjustareminder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 102;
    private static final String TAG = "YourSmallerNotifications";

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i("onReceive", "Alarm created");
        String sa = intent.getStringExtra("NameIs");
        Log.i("The intent is", sa);
        String[] s = sa.split(",");

        Intent thisIntent = new Intent(context, MainActivity.class);
        PendingIntent forThisIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, thisIntent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.raw.aperture)
                .setContentTitle("Please check reminder.")
                .setContentText(s[0] + " at " + s[1])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(forThisIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000});

        notificationManager.notify(TAG, NOTIFICATION_ID, mBuilder.build());
    }
}
