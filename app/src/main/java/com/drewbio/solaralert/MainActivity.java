package com.drewbio.solaralert;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity  {

    // start alarm service
    UpdateReceiver alarm = new UpdateReceiver();

    protected final String TAG = "SolarScreen";
    protected Boolean mRequestingLocationUpdates = false;

    protected static ImageView mUVImageView;
    protected static TextView mUVIndexTextView;
    protected static TextView mLastUpdateTimeTextView;
    protected static TextView mLongitudeTextView;
    protected static TextView mLatitudeTextView;
    protected static TextView mZipCodeTextView;

    public static long UPDATE_INTERVAL_IN_MILLISECONDS;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Rate = "updateRateKey";
    public static final String Enabled = "enabledKey";
    public static final String Phone = "phoneKey";
    public static final String Threshold = "thresholdKey";
    SharedPreferences sharedpreferences;

    /**
     * The fastest rate for active Loc updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-Loc-updates-key";
    protected final static String LOCATION_KEY = "Loc-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    public static Loc loc;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    public static Location mCurrentLocation;
    protected String mLastUpdateTime;
    public static String mZipCode;
    public static String mUVIndex = "0";

    private Intent receiverIntent;
    private PendingIntent alarmIntent;
    private AlarmManager alarmMgr;

    public static List<Intensity> intensityList = new ArrayList<Intensity>();

    public final static String apiURL = "http://iaspub.epa.gov/enviro/efservice/getEnvirofactsUVHOURLY/ZIP/";

    public class Intensity {
        public String uvIndex;
        public int hour;
        public String timeIndicator;

        public Intensity(int hour, String timeIndicator, String uvIndex) {
            this.hour = hour;
            this.timeIndicator = timeIndicator;
            this.uvIndex = uvIndex;
        }

        protected int calculateTime() {
            if(timeIndicator.equalsIgnoreCase("AM") || (timeIndicator.equalsIgnoreCase("PM") && hour == 12)) {
                //System.out.println("a" + hour + timeIndicator);
                return hour;
            } else {
                //System.out.println("b" + hour + timeIndicator);
                return hour + 12;
            }
        }

        @Override
        public String toString() {
            return "TIME: " + hour + timeIndicator + " UV: " + uvIndex;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }



        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mUVImageView = (ImageView) findViewById(R.id.UVImageView);
        mUVIndexTextView = (TextView) findViewById(R.id.uvIndexLbl);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.lastUpdateLbl);
        mLongitudeTextView = (TextView) findViewById(R.id.longitudeLbl);
        mLatitudeTextView = (TextView) findViewById(R.id.latitudeLbl);
        mZipCodeTextView = (TextView) findViewById(R.id.zipCodeLbl);

        // Create a new service client and bind our activity to this service
        //scheduleClient = new ScheduleClient(this);
        //scheduleClient.doBindService();

        //System.out.println(mRequestingLocationUpdates);
        //if(!mRequestingLocationUpdates) {

           // buildGoogleApiClient();
        //}
        loc = new Loc(this);
        alarm.setAlarm(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the latitude, the longitude, and the last Loc time in the UI.
     */
    public static void updateUI() {
        if (Loc.mCurrentLocation != null) {
            mUVIndexTextView.setText("Solar Intensity: " + mUVIndex);
            mLatitudeTextView.setText("Latitude: " + String.valueOf(Loc.mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText("Longitude: " + String.valueOf(Loc.mCurrentLocation.getLongitude()));
            mLastUpdateTimeTextView.setText("Last Updated: " + Loc.mLastUpdateTime);
            mZipCodeTextView.setText("Zip Code: " + mZipCode);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentLocation = loc.getLocation();
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause Loc updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // Loc updates if the user has requested them.

        mCurrentLocation = loc.getLocation();
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop Loc updates to save battery, but don't disconnect the GoogleApiClient object.
        //if (mGoogleApiClient.isConnected()) {
        //    stopLocationUpdates();
        //}
    }
    /*

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }
    */

    public void sunscreenBtn_Click(View view) {


        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("Sunscreen Reminder");
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.reminderNumberPicker);
        np.setMaxValue(3); // list size
        np.setMinValue(0);
        np.setDisplayedValues(new String[]{"30 Minutes", "60 Minutes", "90 Minutes", "120 Minutes"});
        np.setWrapSelectorWheel(false);
        //np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int reminderDuration = ((np.getValue() + 1) * 30) * 60 * 1000; // convert to minutes then to milliseconds
                alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                receiverIntent = new Intent(getBaseContext(), ReminderAlarmReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(getBaseContext(), 0, receiverIntent, 0);
                alarmMgr.set(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis() + reminderDuration, alarmIntent);
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });
        d.show();
    }

    public void updateBtn_Click(View view) {
        Toast.makeText(this, "Updating now", Toast.LENGTH_SHORT).show();
        //startLocationUpdates();
        mCurrentLocation = loc.getLocation();
    }
}
