package com.drewbio.solaralert;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Intensity {
    public static Map<Integer, Integer> intensityMap = new HashMap<Integer, Integer>();

    public static int getCurrentIntensity() {
        final String TAG = "Intensity.java";

/*        // set your json string url here
        String urlString = MainActivity.apiURL + "59801" + "/JSON";

        JSONArray jsonArray = null;
        Map<Integer, Integer> jsonMap = new HashMap<>();


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

                for(int i = 0; i < 24; i++) {
                    if(!jsonMap.containsKey(i))
                        jsonMap.put(i, 0);
                }

                intensityMap.clear();
                intensityMap.putAll(jsonMap);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

        new AsyncTaskParseJson().execute();


        int currentIntensity = 0;
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        //System.out.println("current hour: " + currentHour);

        currentIntensity = intensityMap.get(currentHour);

        return currentIntensity;
    }
}