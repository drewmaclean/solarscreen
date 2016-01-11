package com.drewbio.solaralert;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Loc implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    protected final String TAG = "SolarScreen";
    protected static GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    public static String mLastUpdateTime;
    public static Location mCurrentLocation;
    private Context context;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Rate = "updateRateKey";
    public static final String Enabled = "enabledKey";
    public static final String Phone = "phoneKey";
    public static final String Threshold = "thresholdKey";
    SharedPreferences sharedpreferences;

    public Loc(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        System.out.println("Loc");
        this.context = context;
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    public Location getLocation() {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        //toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        else {
            if (mCurrentLocation != null) {
                update(mCurrentLocation);
            }
        }
        //update(mCurrentLocation);
        return mCurrentLocation;
    }

    /**
     * Requests Loc updates from the FusedLocationApi.
     */
    public void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        // It is a good practice to remove Loc requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent Loc updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    public synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        //long rate = sharedpreferences.getString(Rate, "1 hour");
        //UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 60 * 60;

        // Sets the desired interval for active Loc updates. This interval is
        // inexact. You may not receive updates at all if no Loc sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting Loc at a faster interval.
        //mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active Loc updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        //mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("Connected to GoogleApiClient");

        // If the initial Loc was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests Loc updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial Loc in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new Loc, and then changes the device orientation, the original Loc
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                MainActivity.mZipCode = Util.getAddressFromLocation(context, mCurrentLocation).getPostalCode();
                MainActivity.updateUI();
            }
            catch (Exception e) {
                Log.e(TAG, "onConnected exception: " + e);
            }
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start Loc updates.
        //if (mRequestingLocationUpdates) {
        startLocationUpdates();
        //}
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        //Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        //Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Callback that fires when the Loc changes.
     */
    @Override
    public void onLocationChanged(android.location.Location location) {
        update(location);
        //MainActivity.mUVIndex = Util.getCurrentIntensity();

        //updateUI();
        //Toast.makeText(this, "Loc changed", Toast.LENGTH_SHORT).show();
        stopLocationUpdates(); // stop updates because Loc is found
        //getUVIndex();
        //checkThreshold();
        //makeNotification(this);
        //AppWidgetProvider.update(this, mCurrentLocation);


    }

    public void update(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        MainActivity.mZipCode = Util.getAddressFromLocation(context, mCurrentLocation).getPostalCode();
        //String urlString = MainActivity.apiURL + MainActivity.mZipCode;
        //new Util().fetchCurrentIntensity(urlString);
        MainActivity.mUVIndex = String.valueOf(Intensity.getCurrentIntensity());
        MainActivity.updateUI();
        checkThreshold();
        makeNotification();
        AppWidgetProvider.update(context, mCurrentLocation);
    }

    public void checkThreshold() {
        Boolean phoneEnabled = sharedpreferences.getBoolean(Enabled, false);
        String phoneNumber = sharedpreferences.getString(Phone, "");
        int phoneThreshold = sharedpreferences.getInt(Threshold, 11); // 11 is max
        if(phoneEnabled && !phoneNumber.isEmpty() && Intensity.getCurrentIntensity() > phoneThreshold) {
            String message = "The current Solar Intensity at zip code: " + MainActivity.mZipCode + " is " + Intensity.getCurrentIntensity();

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        }
    }

    public void makeNotification() {

        int img = 0;

        com.luckycatlabs.sunrisesunset.dto.Location location =
                new com.luckycatlabs.sunrisesunset.dto.Location(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, TimeZone.getDefault());
        Calendar officialSunrise = calculator.getOfficialSunriseCalendarForDate(Calendar.getInstance());
        Calendar officialSunset = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());

        //System.out.println("sunrise: " + officialSunrise.get(Calendar.HOUR_OF_DAY));
        //System.out.println("sunset: " + officialSunset.get(Calendar.HOUR_OF_DAY));

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour <= officialSunrise.get(Calendar.HOUR_OF_DAY) || currentHour >= officialSunset.get(Calendar.HOUR_OF_DAY)) {
            img = R.drawable.notification_night;
        } else {
            // set the notification icon and the imageView to be displayed.
            switch (Intensity.getCurrentIntensity()) {
                case 0:
                    img = R.drawable.notification_0;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index);
                    break;
                case 1:
                    img = R.drawable.notification_1;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_1);
                    break;
                case 2:
                    img = R.drawable.notification_2;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_2);
                    break;
                case 3:
                    img = R.drawable.notification_3;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_3);
                    break;
                case 4:
                    img = R.drawable.notification_4;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_4);
                    break;
                case 5:
                    img = R.drawable.notification_5;
                    MainActivity. mUVImageView.setImageResource(R.drawable.un_index_5);
                    break;
                case 6:
                    img = R.drawable.notification_6;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_6);
                    break;
                case 7:
                    img = R.drawable.notification_7;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_7);
                    break;
                case 8:
                    img = R.drawable.notification_8;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_8);
                    break;
                case 9:
                    img = R.drawable.notification_9;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_9);
                    break;
                case 10:
                    img = R.drawable.notification_10;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_10);
                    break;
                case 11:
                    img = R.drawable.notification_11;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index_11);
                    break;
                default:
                    img = R.drawable.notification_night;
                    MainActivity.mUVImageView.setImageResource(R.drawable.un_index);
                    break;
            }
        }

        Notification notification = new Notification.Builder(context)
                .setContentTitle("Sun Intensity")
                .setContentText("Current intensity: " + Intensity.getCurrentIntensity())
                .setSmallIcon(img)
                .setOngoing(true)
                .build();

// Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notification);
    }
}
