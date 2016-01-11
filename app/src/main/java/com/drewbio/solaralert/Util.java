package com.drewbio.solaralert;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Switch;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Util {

    public static long convertToMilliseconds(String time) {
        int num = Integer.parseInt(time.replaceAll("[^0-9]", ""));
        long milliseconds = 0;
        if (time.contains("min")) {
            milliseconds = num * 60 * 1000;
        } else if (time.contains("hour")) {
            milliseconds = num * 60 * 60 * 1000;
        } else {
            milliseconds = num * 1000;
        }
        return milliseconds;
    }

    public static int parseTime(String dateTime) throws ParseException {
        String time = dateTime.substring(dateTime.length() - 5, dateTime.length() );

        SimpleDateFormat displayFormat = new SimpleDateFormat("HH");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a");
        Date convertedTime = parseFormat.parse(time);

        return Integer.parseInt(displayFormat.format(convertedTime));
    }

    public static Address getAddressFromLocation(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context);
        Address address = new Address(Locale.getDefault());
        try {
            List<Address> addr = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addr.size() > 0) {
                address = addr.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

/*

    public void fetchCurrentIntensity(String urlString) {
        new CallAPI().execute(urlString);
    }


    private class CallAPI extends AsyncTask<java.lang.String, java.lang.String, java.lang.String> {
        List<Intensity> resultList;

        @Override
        protected java.lang.String doInBackground(java.lang.String... params) {
            java.lang.String urlString = params[0]; // URL to call
            java.lang.String resultToDisplay = "";
            InputStream in = null;
            List<Intensity> result = null;

            // HTTP Get
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                return e.getMessage();
            }

            // Parse XML
            XmlPullParserFactory pullParserFactory;
            try {
                pullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = pullParserFactory.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                //resultToDisplay = parseXML(parser);
                result = parseXML(parser);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (result != null) {
                intensityList = result; // store for widget
            }


            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            //System.out.println("current hour: " + currentHour);

            boolean found = false;
            for (Intensity uv : result) {
                int time = uv.calculateTime();
                //System.out.println("checking time: " + time);
                if (time == currentHour) {

                    resultToDisplay = uv.uvIndex;
                    found = true;
                    break;
                }
            }
            if (!found)
                resultToDisplay = "0";


            MainActivity.mUVIndex = resultToDisplay;
            return resultToDisplay;
        }

        protected List<String> onPostExecute(List<String> result) {
            return result;
        }

        private List<Intensity> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
            int eventType = parser.getEventType();

            resultList = new ArrayList<>();
            java.lang.String[] row = new java.lang.String[3];

            while (eventType != XmlPullParser.END_DOCUMENT) {
                java.lang.String name = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        name = parser.getName();
                        if (name.equals("DATE_TIME")) {
                            java.lang.String s = parser.nextText();
                            java.lang.String[] tokens = s.split("[ ]");
                            //System.out.println("TIME: " + tokens[1] + tokens[2]);
                            row[0] = tokens[1];
                            row[1] = tokens[2];
                        } else if (name.equals("UV_VALUE")) {
                            row[2] = parser.nextText();
                            //System.out.println("UV:" + row[2]);
                            Intensity result = new Intensity(Integer.parseInt(row[0]), row[1], row[2]);
                            resultList.add(result);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                } // end switch
                eventType = parser.next();
            } // end while
            return resultList;
        }
    } // end CallAPI
*/

}
