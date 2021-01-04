package com.example.knowyourgovernment;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OfficialDownloader implements Runnable{
    private static final String TAG = "OfficialDownloader";
    private static final String API_key = "AIzaSyAIV8YTg6NYqeMrLRwtPCtlnZpJBOZfS2A";
    private String postal_code;
    private MainActivity mainActivity;

    public OfficialDownloader(MainActivity mainActivity, String postal_code) {
        this.mainActivity = mainActivity;
        this.postal_code = postal_code;
    }

    @Override
    public void run() {
        ArrayList<Official> officialList = new ArrayList<>();
        String location;
        String dataURL="https://www.googleapis.com/civicinfo/v2/representatives?key="+API_key+"&address="+postal_code;
        String data=getStockDataFromURL(dataURL);
        Log.d(TAG, "run2222: "+data);
        if(checkData(data)) {
            location = parseJSONLocateMe(data);
            Log.d(TAG, "parseJSONdata is: "+location);
            officialList = parseJSON(data);
            sendData(officialList, location);
        }
    }

    private boolean checkData(String data) {
        if (data == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.downloadFailed();
                }
            });
        }
        else {
            return true;
        }
        return false;
    }

    private void sendData(final ArrayList<Official> t, final String l) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.updateData(t,l);
            }
        });
    }

    private String getStockDataFromURL(String dataURL) {

        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return (null);
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run:data" + sb.toString());
            return(sb.toString());
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            return(null);
        }
    }

    private String parseJSONLocateMe(String data) {
        String location="";
        String city="";
        String state="";
        String zip="";
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject normalizedInputObject=new JSONObject(jsonObject.getString("normalizedInput"));

            try{
                city=normalizedInputObject.getString("city");
            }catch (JSONException e) {
                e.printStackTrace();
            }

            try{
                state=normalizedInputObject.getString("state");
            }catch (JSONException e) {
                e.printStackTrace();
            }

            try{
                zip=normalizedInputObject.getString("zip");
            }catch (JSONException e) {
                e.printStackTrace();
            }

            location = city+((city.trim().equals("")) ? "": ", ")+state+((state.trim().equals("")) ? "": " ")+zip;
            Log.d(TAG, "parseJSONdatadata is: "+city+" location is: "+location);
            return location;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return null;
    }

    private ArrayList<Official> parseJSON(String s) {
        Log.d(TAG, "parseJSONnnnnn: "+s);
        ArrayList<Official> tempList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray offices = new JSONArray(jsonObject.getString("offices"));

            for (int i = 0; i < offices.length();i++)
            {
                try{
                JSONObject office = (JSONObject) offices.get(i);
                JSONArray officialIndices = office.getJSONArray("officialIndices");

                for (int j = 0; j< officialIndices.length();j++)
                {
                  try{
                    int position = Integer.parseInt(officialIndices.get(j).toString());
                    JSONArray officials = new JSONArray(jsonObject.getString("officials"));
                    JSONObject officialInfo = officials.getJSONObject(position);

                    Official official = new Official();

                    //title
                    String title="";
                    try{
                        title=office.getString("name");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    official.setTitle(title);

                    //name:
                    String name="";
                    try{
                        name=officialInfo.getString("name");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    official.setName(name);

                    //party:
                    String party="";
                    try{
                        party=officialInfo.getString("party");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    official.setParty(party);

                    //Address:
                    JSONArray addressLines = new JSONArray(officialInfo.getString("address"));
                    JSONObject addressLine = new JSONObject(addressLines.get(0).toString());

                    String line1 = "";
                    try{
                        line1 = addressLine.getString("line1");
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    String line2 = "";
                    try{
                        line2 = addressLine.getString("line2");
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    String city1 = "";
                    try{
                        city1 = addressLine.getString("city");
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    String state1 = "";
                    try{
                        state1 = addressLine.getString("state");
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    String zip1 = "";
                    try{
                        zip1 = addressLine.getString("zip");
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    String address = line1 + ((line1.trim().equals("")) ? "":" ")+ line2 +((line2.trim().equals("")) ? "":" ")+ city1 +((city1.trim().equals("")) ? "": ", ")+ state1 + ((state1.trim().equals("")) ? "": " ")+ zip1;
                    official.setAddress(address);

                    //Phone number:
                    String phone ="";
                    try{
                        phone=officialInfo.getJSONArray("phones").getString(0);
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    official.setPhone(phone);

                    //URL:
                    String URL="";
                    try{
                        URL = officialInfo.getJSONArray("urls").getString(0);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    official.setUrl(URL);

                    //email:
                    String email="";
                    try{
                        email = officialInfo.getJSONArray("emails").getString(0);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    official.setEmail(email);

                    //Phone Url:
                    String photoURL="";
                    try{
                        photoURL = officialInfo.getString("photoUrl");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    official.setPhotoURL(photoURL);

                    //social media
                    try {
                        JSONArray channels =new JSONArray(officialInfo.getString("channels"));

                        for (int k = 0; k < channels.length(); k++) {
                            try {
                                JSONObject channel = (JSONObject) channels.get(k);

                                String facebook = "", twitter = "", youtube = "";

                                if (channel.getString("type").equals("Facebook")) {
                                    facebook = channel.getString("id");
                                    official.setFacebook(facebook);
                                }
                                if (channel.getString("type").equals("Twitter")) {
                                    twitter = channel.getString("id");
                                    official.setTwitter(twitter);
                                }
                                if (channel.getString("type").equals("YouTube")) {
                                    youtube = channel.getString("id");
                                    official.setYoutube(youtube);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    Log.d(TAG, "parseJSONnnnnnnnn: "+official.getTitle()+ " "+official.getName()+ " Facebookkkk idd is"+official.getFacebook());
                    tempList.add(official);
                } catch (Exception e) {
                Log.d(TAG, "parseJSON: " + e.getMessage());
                e.printStackTrace();
            }
                }
            } catch (Exception e) {
                Log.d(TAG, "parseJSON: " + e.getMessage());
                e.printStackTrace();
            }
            }
            return tempList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}