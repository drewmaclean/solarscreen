package com.drewbio.solaralert;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class JsonParser {

    final String TAG = "JsonParser.java";

    private InputStream in = null;
    private JSONArray jArr = null;
    private String json = "";

    public JSONArray getJSONFromUrl(String urlString) {

        // HTTP Get
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            //sb.append("{");
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
            json = sb.toString();

        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jArr = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        // return JSON String
        return jArr;
    }
}