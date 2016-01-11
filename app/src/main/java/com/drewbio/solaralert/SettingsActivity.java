package com.drewbio.solaralert;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;


public class SettingsActivity extends Activity {

    public Switch mUpdateRateSwitch;
    public Switch mPhoneSwitch;
    public EditText mPhoneEditText;
    public Spinner mPhoneThresholdSpinner;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Rate = "updateRateKey";
    public static final String Enabled = "enabledKey";
    public static final String Phone = "phoneKey";
    public static final String Threshold = "thresholdKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mUpdateRateSwitch = (Switch) findViewById(R.id.updateRateSwitch);
        mPhoneSwitch = (Switch) findViewById(R.id.phoneCheckBox);
        mPhoneEditText = (EditText) findViewById(R.id.phoneEditText);
        mPhoneThresholdSpinner = (Spinner) findViewById(R.id.phoneThresholdSpinner);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUpdateRateSwitch.setChecked(sharedpreferences.getBoolean(Rate, false));
        mPhoneEditText.setText(sharedpreferences.getString(Phone, ""));
        mPhoneThresholdSpinner.setSelection(sharedpreferences.getInt(Threshold, 10));
        mPhoneSwitch.setChecked(sharedpreferences.getBoolean(Enabled, false));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(Rate, mUpdateRateSwitch.isChecked());
        editor.putString(Phone, mPhoneEditText.getText().toString());
        editor.putInt(Threshold, mPhoneThresholdSpinner.getSelectedItemPosition());
        editor.putBoolean(Enabled, mPhoneSwitch.isChecked());

        editor.apply();
        Toast.makeText(this, "Saving Changes", Toast.LENGTH_LONG).show();
    }

    public void travelImageButton_Click(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Turn this on while traveling to receive more frequent location updates.");
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void phoneImageButton_Click(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Send a text message if the solar intensity exceeds the threshold.");
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
