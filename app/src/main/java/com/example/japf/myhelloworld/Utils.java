package com.example.japf.myhelloworld;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by japf on 16/06/2016.
 */
public class Utils {

    public static String[] getCountryNamesFromJSON(String jsonResponse) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray jsonArray = jsonObject.getJSONArray("geonames");

        if (jsonArray == null){
            Log.w("MainActivityFragment", "getCountryNamesFromJSON ---> jsonArray is null");
            return null;
        }

        String[] countryNames = new String[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jCountry = jsonArray.getJSONObject(i);
            String name = jCountry.getString("countryName");
            countryNames[i] = name;
        }
        return countryNames;
    }

    public static String getDataForCountryJSON(String jsonResponse, String countryName, String fieldName) throws JSONException{

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray jsonArray = jsonObject.getJSONArray("geonames");

        if (jsonArray == null){
            Log.w("MainActivityFragment", "getDataForCountryJSON ---> jsonArray is null");
            return null;
        }

        String[] countryNames = new String[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jCountry = jsonArray.getJSONObject(i);
            String name = jCountry.getString("countryName");
            if (name.equals(countryName)){
                String data = jCountry.getString(fieldName);
                return data;
            }
        }
        return null;
    }

    public static String getDataForCountryJSON(String jsonResponse, String countryName) throws JSONException{

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray jsonArray = jsonObject.getJSONArray("geonames");

        if (jsonArray == null){
            Log.w("MainActivityFragment", "getDataForCountryJSON ---> jsonArray is null");
            return null;
        }

        String[] countryNames = new String[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jCountry = jsonArray.getJSONObject(i);
            String name = jCountry.getString("countryName");
            if (name.equals(countryName)){
                return jCountry.toString();
            }
        }
        return null;
    }

    public static String getFieldForCountryJSON(String jCountry, String key){
        try {
            JSONObject jsonObject = new JSONObject(jCountry);
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
