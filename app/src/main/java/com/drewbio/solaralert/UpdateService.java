package com.drewbio.solaralert;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.util.Calendar;
import java.util.TimeZone;

public class UpdateService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.i("UpdateService", "Start service @ " + SystemClock.elapsedRealtime());

        MainActivity.mUVIndex = String.valueOf(Intensity.getCurrentIntensity());
        makeNotification();
        AppWidgetProvider.update(getApplicationContext(), Loc.mCurrentLocation);

        //Log.i("UpdateService", "Completed service @ " + SystemClock.elapsedRealtime());
        UpdateReceiver.completeWakefulIntent(intent);
    }

    public void makeNotification() {

        int img = 0;

        int officialSunrise;
        int officialSunset;

        try {
            com.luckycatlabs.sunrisesunset.dto.Location location =
                    new com.luckycatlabs.sunrisesunset.dto.Location(Loc.mCurrentLocation.getLatitude(), Loc.mCurrentLocation.getLongitude());
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, TimeZone.getDefault());
            Calendar officialSunriseCal = calculator.getOfficialSunriseCalendarForDate(Calendar.getInstance());
            Calendar officialSunsetCal = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());
            officialSunrise = officialSunriseCal.get(Calendar.HOUR_OF_DAY);
            officialSunset = officialSunsetCal.get(Calendar.HOUR_OF_DAY);
        } catch (Exception e) {
            //System.out.println(e);
            officialSunrise = 9;
            officialSunset = 18;
        }

        //System.out.println("sunrise: " + officialSunrise.get(Calendar.HOUR_OF_DAY));
        //System.out.println("sunset: " + officialSunset.get(Calendar.HOUR_OF_DAY));

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour < officialSunrise || currentHour > officialSunset) {
            img = R.drawable.notification_night;
        } else {
            // set the notification icon and the imageView to be displayed.
            switch (Intensity.getCurrentIntensity()) {
                case 0:
                    img = R.drawable.notification_0;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index);
                    break;
                case 1:
                    img = R.drawable.notification_1;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_1);
                    break;
                case 2:
                    img = R.drawable.notification_2;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_2);
                    break;
                case 3:
                    img = R.drawable.notification_3;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_3);
                    break;
                case 4:
                    img = R.drawable.notification_4;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_4);
                    break;
                case 5:
                    img = R.drawable.notification_5;
                    //MainActivity. mUVImageView.setImageResource(R.drawable.un_index_5);
                    break;
                case 6:
                    img = R.drawable.notification_6;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_6);
                    break;
                case 7:
                    img = R.drawable.notification_7;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_7);
                    break;
                case 8:
                    img = R.drawable.notification_8;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_8);
                    break;
                case 9:
                    img = R.drawable.notification_9;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_9);
                    break;
                case 10:
                    img = R.drawable.notification_10;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_10);
                    break;
                case 11:
                    img = R.drawable.notification_11;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index_11);
                    break;
                default:
                    img = R.drawable.notification_night;
                    //MainActivity.mUVImageView.setImageResource(R.drawable.un_index);
                    break;
            }
        }

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Sun Intensity")
                .setContentText("Current intensity: " + Intensity.getCurrentIntensity())
                .setSmallIcon(img)
                .setOngoing(true)
                .build();

// Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notification);
    }
}
