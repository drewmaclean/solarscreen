package com.drewbio.solaralert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

public class UpdateReceiver extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UpdateReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        // schedule alarm to fire approximately 5 min after the hour
        calendar.set(Calendar.HOUR_OF_DAY, currentHour );
        calendar.set(Calendar.MINUTE, 5);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, alarmIntent);
    }

        @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, UpdateService.class);
        // Start the service, keeping the device awake while it is launching.
        //Log.i("UpdateReceiver", "OnReceive @ " + SystemClock.elapsedRealtime());
        startWakefulService(context, service);

    }
}