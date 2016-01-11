package com.drewbio.solaralert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

public class ReminderAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Toast.makeText(context,"received",Toast.LENGTH_LONG).show();

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("Apply Sunscreen")
                .setContentText("Time to apply sunscreen again")
                .setSmallIcon(R.drawable.notification_alert)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0))
                .build();

        notificationManager.notify(0, notification);
    }
}