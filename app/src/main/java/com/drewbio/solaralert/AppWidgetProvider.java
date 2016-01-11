package com.drewbio.solaralert;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;
import java.util.TimeZone;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.app_widget);

        int officialSunrise;
        int officialSunset;

        try {
            //MainActivity ma = new MainActivity();

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
            officialSunrise = 9;
            officialSunset = 18;
        }

            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (currentHour <= officialSunrise || currentHour >= officialSunset) {
                remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int1);
                remoteViews.setTextViewText(R.id.textView, "ZZZ");
            } else {
                remoteViews.setTextViewText(R.id.textView, MainActivity.mUVIndex);

                int intensityValue = 0;
/*                Boolean found = false;
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
                    intensityValue = "0";*/
                intensityValue = Intensity.getCurrentIntensity();

                switch (intensityValue) {
                    case 0:
                    case 1:
                    case 2:
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int1);
                        break;
                    case 3:
                    case 4:
                    case 5:
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int2);
                        break;
                    case 6:
                    case 7:
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int3);
                        break;
                    case 8:
                    case 9:
                    case 10:
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int4);
                        break;
                    case 11:
                        remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int5);
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
            officialSunrise = 9;
            officialSunset = 18;
        }

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour <= officialSunrise || currentHour >= officialSunset) {
            remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int1);
            remoteViews.setTextViewText(R.id.textView, "ZZZ");
        } else {
            remoteViews.setTextViewText(R.id.textView, MainActivity.mUVIndex);

            int intensityValue = 0;
/*            Boolean found = false;
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
                intensityValue = "0";*/
            intensityValue = Intensity.getCurrentIntensity();

            switch (intensityValue) {
                case 0:
                case 1:
                case 2:
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int1);
                    break;
                case 3:
                case 4:
                case 5:
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int2);
                    break;
                case 6:
                case 7:
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int3);
                    break;
                case 8:
                case 9:
                case 10:
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int4);
                    break;
                case 11:
                    remoteViews.setImageViewResource(R.id.imageView, R.drawable.icon_int5);
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