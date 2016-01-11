package com.drewbio.solaralert;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

    final String TAG = "AsyncTaskParseJson.java";

    // set your json string url here
    String urlString = MainActivity.apiURL + MainActivity.mZipCode + "/JSON";

    JSONArray jsonArray = null;
    Map<Integer, Integer> jsonMap = new HashMap<>();

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... arg0) {


    }

    @Override
    protected void onPostExecute(String strFromDoInBg) {
        for(int i = 0; i < 24; i++) {
            if(!jsonMap.containsKey(i))
                jsonMap.put(i, 0);
        }

        Intensity.intensityMap.clear();
        Intensity.intensityMap.putAll(jsonMap);
    }

    private void getJSON() {
        try {

            // instantiate our json parser
            JsonParser jParser = new JsonParser();

            // get json string from url
            jsonArray = jParser.getJSONFromUrl(urlString);

            // get the array of users
            //jsonArray = json.getJSONArray("");

            // loop through all users
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject c = jsonArray.getJSONObject(i);

                // Storing each json item in variable
                String dateTime = c.getString("DATE_TIME");
                int uvValue = c.getInt("UV_VALUE");

                // show the values in our logcat
                Log.e(TAG, "DATE_TIME: " + dateTime + ", UV_VALUE: " + uvValue);

                int time = Util.parseTime(dateTime);
                jsonMap.put(time, uvValue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
