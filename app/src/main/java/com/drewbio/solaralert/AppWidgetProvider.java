package com.drewbio.solaralert;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.drewbio.solaralert.R;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;
import java.util.TimeZone;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {
    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.app_widget);

        int officialSunrise;
        int officialSunset;

        try {
            MainActivity ma = new MainActivity();

            Location location = new Location(MainActivity.mCurrentLocation.getLatitude(), MainActivity.mCurrentLocation.getLongitude());
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, TimeZone.getDefault());
            Calendar officialSunriseCal = calculator.getOfficialSunriseCalendarForDate(Calendar.getInstance());
            Calendar officialSunsetCal = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());
            officialSunrise = officialSunriseCal.get(Calendar.HOUR_OF_DAY);
            officialSunset = officialSunsetCal.get(Calendar.HOUR_OF_DAY);

            //System.out.println("sunrise: " + officialSunriseCal.get(Calendar.HOUR_OF_DAY));
            //System.out.println("sunset: " + officialSunsetCal.get(Calendar.HOUR_OF_DAY));

        } catch (Exception e) {
            //System.out.println(e);
            officialSunrise = 7;
            officialSunset = 20;
        }

            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (currentHour <= officialSunrise || currentHour >= officialSunset) {
                remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_night);
                remoteViews.setTextViewText(R.id.textView, "ZZZ");
            } else {
                remoteViews.setTextViewText(R.id.textView, MainActivity.mUVIndex);

                String intensityValue = "";
                Boolean found = false;
                for (MainActivity.Intensity intensity : MainActivity.intensityList) {
                    int time = intensity.calculateTime();
                    //System.out.println(time);
                    if (time == currentHour) {
                        intensityValue = intensity.uvIndex;
                        found = true;
                        break;
                    }
                }
                if (!found)
                    intensityValue = "0";

                switch (intensityValue) {
                    case "0":
                    case "1":
                    case "2":
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_low);
                        break;
                    case "3":
                    case "4":
                    case "5":
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_mild);
                        break;
                    case "6":
                    case "7":
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_mid);
                        break;
                    case "8":
                    case "9":
                    case "10":
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_high);
                        break;
                    case "11":
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_ultra);
                        break;
                    default:
                        break;
                }
            }

            ComponentName myWidget = new ComponentName(context,
                    AppWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.updateAppWidget(myWidget, remoteViews);



        //Toast.makeText(context, "onUpdate", Toast.LENGTH_LONG).show();

    }

    public static void update(Context context, android.location.Location location){

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.app_widget);

        int officialSunrise;
        int officialSunset;

        try {
            Location latLongLoc = new Location(location.getLatitude(), location.getLongitude());
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(latLongLoc, TimeZone.getDefault());
            Calendar officialSunriseCal = calculator.getOfficialSunriseCalendarForDate(Calendar.getInstance());
            Calendar officialSunsetCal = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());
            officialSunrise = officialSunriseCal.get(Calendar.HOUR_OF_DAY);
            officialSunset = officialSunsetCal.get(Calendar.HOUR_OF_DAY);

            //System.out.println("sunrise: " + officialSunriseCal.get(Calendar.HOUR_OF_DAY));
            //System.out.println("sunset: " + officialSunsetCal.get(Calendar.HOUR_OF_DAY));

        } catch (Exception e) {
            //System.out.println(e);
            officialSunrise = 7;
            officialSunset = 20;
        }

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour <= officialSunrise || currentHour >= officialSunset) {
            remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_night);
            remoteViews.setTextViewText(R.id.textView, "ZZZ");
        } else {
            remoteViews.setTextViewText(R.id.textView, MainActivity.mUVIndex);

            String intensityValue = "";
            Boolean found = false;
            for (MainActivity.Intensity intensity : MainActivity.intensityList) {
                int time = intensity.calculateTime();
                //System.out.println(time);
                if (time == currentHour) {
                    intensityValue = intensity.uvIndex;
                    found = true;
                    break;
                }
            }
            if (!found)
                intensityValue = "0";

            switch (intensityValue) {
                case "0":
                case "1":
                case "2":
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_low);
                    break;
                case "3":
                case "4":
                case "5":
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_mild);
                    break;
                case "6":
                case "7":
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_mid);
                    break;
                case "8":
                case "9":
                case "10":
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_high);
                    break;
                case "11":
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.sun_ultra);
                    break;
                default:
                    break;
            }
        }

        ComponentName myWidget = new ComponentName(context,
                AppWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);

    }
}