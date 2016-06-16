package com.example.japf.myhelloworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import static com.example.japf.myhelloworld.R.menu.menu_fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    String jsonCountryData = "Empty Data";
    List<String> countryNames = new ArrayList<String>();
    ArrayAdapter<String> countryNamesListAdapter;

    String countryQuery ="http://api.geonames.org/countryInfoJSON?formatted=true&lang=it&country=&username=coursejun2016&style=full";
    String testQuery ="http://api.geonames.org/findNearbyStreetsOSMJSON?formatted=true&lat=37.451&lng=-122.18&username=coursejun2016&style=full";


    String selectedCountry = "DE";
    String selectedLanguage ="it";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences mySettings = getActivity().getSharedPreferences("pref_general", 0);
        selectedCountry = mySettings.getString("pref_country_key", "DE");

        FetchCountryDataTask getDataTask = new FetchCountryDataTask();
        getDataTask.execute();
    }


    /*
    @Override
    public void onStart(){
        FetchCountryDataTask getDataTask = new FetchCountryDataTask();
        getDataTask.execute();
    }
    */


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchCountryDataTask getDataTask = new FetchCountryDataTask();
            getDataTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        countryNamesListAdapter =
                new ArrayAdapter<String>(
                        getActivity(),          // The current context (this activity)
                        R.layout.my_list_row,   // The name of the layout ID.
                        R.id.country_name,      // The ID of the textview to populate.
                        countryNames);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.country_list_view);
        listView.setAdapter(countryNamesListAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String cName = (String)parent.getItemAtPosition(position);
                        Log.e("MainActivityFragment", "*******!!! OnItemClick ----------> " + cName);
                        String cData = "cData: noData";

                        try {
                            cData = Utils.getDataForCountryJSON(jsonCountryData, cName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //String path = "C:\\Users\\japf\\Documents\\GitHub\\MyHelloWorld\\app\\src\\main\\res\\xml";
                        SharedPreferences sharedPref = getContext().getSharedPreferences("pref_general", Context.MODE_PRIVATE);
                        //SharedPreferences sharedPref = getContext().getSharedPreferences(path, Context.MODE_PRIVATE);
                        String prefs = sharedPref.contains("pref_show_capital")?"yes":"no";
                        String dataToShow = "\n" + "!!!!!" + prefs + "\n\n";

                        Log.e("MainActivityFragment", "prefs ---> " + dataToShow);


                        String countryData = cData;
                        Intent intent = new Intent(getActivity(), CountryActivity.class)
                                .putExtra(Intent.EXTRA_TEXT, countryData);
                        startActivity(intent);
                    }
                }
        );
        return rootView;
    }


    class FetchCountryDataTask extends AsyncTask<Void, Void, String> {
        private final String LOG_TAG = FetchCountryDataTask.class.getSimpleName();

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            String jsonMsg = null;
            String baseUrl = countryQuery;
            urlConnection = connect(baseUrl);
            jsonMsg = getDataAndCloseConnection(urlConnection);
            return jsonMsg;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                jsonCountryData = result;
                countryNamesListAdapter.clear();
                try {
                    String [] data = Utils.getCountryNamesFromJSON(jsonCountryData);
                    for(String countryName : data) {
                        countryNamesListAdapter.add(countryName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        private HttpURLConnection connect(String baseUrl) {
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(baseUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
                urlConnection = null;
            }
            return urlConnection;
        }

        private String getDataAndCloseConnection(HttpURLConnection urlConnection) {
            String rvcMsg = null;
            BufferedReader reader = null;

            try {
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() != 0) {
                        rvcMsg = buffer.toString();
                    }
                }
            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error accesing stream", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                String [] data = Utils.getCountryNamesFromJSON(rvcMsg);
                StringBuffer bData = new StringBuffer();
                for (int i = 0; i < data.length - 1; i++){
                    bData.append(data[i] + ", ");
                }
                bData.append(data[data.length-1] + "\n");
                Log.e("MainActivityFragment", bData.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return rvcMsg;
        }
    }
}
