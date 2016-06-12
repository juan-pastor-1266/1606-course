package com.example.japf.myhelloworld;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.japf.myhelloworld.R.menu.menu_fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    List<String> contactsList;
    ArrayAdapter<String> contactsListAdapter;

    String selectedCountry = "DE";
    String selectedLanguage ="it";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        SharedPreferences mySettings = getActivity().getSharedPreferences("pref_general", 0);
        Log.i("MainActivityFragment", mySettings.getAll().toString());
        selectedCountry = mySettings.getString("pref_country_key", "DE");
        Log.i("MainActivityFragment", "Country -> " + selectedCountry);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(menu_fragment, menu);
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

        // Fake data to populate the list of contacts
        String[] names = {
                "John", "Mary", "Joe", "Jane", "Elena", "Maud", "Phil", "Teo", "Nemo"
        };

        Integer[] images = {
                R.drawable.face_1, R.drawable.face_2, R.drawable.face_3,
                R.drawable.face_4, R.drawable.face_5, R.drawable.face_6,
                R.drawable.face_7, R.drawable.face_8, R.drawable.face_9
        };

        contactsList = new ArrayList<String>(Arrays.asList(names));
        contactsListAdapter =
                new MyListAdapter(
                        getActivity(), names, images);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.contact_list_view);
        listView.setAdapter(contactsListAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String countryData = "{\"geonames\": [{\n" + "  \"continent\": \"VOID\",\n" + "}]}";

                        Intent intent = new Intent(getActivity(), CountryActivity.class)
                                .putExtra(Intent.EXTRA_TEXT, countryData);
                        startActivity(intent);

                    }
                }
        );

        return rootView;
    }



class FetchCountryDataTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = FetchCountryDataTask.class.getSimpleName();

    @Override
    protected Void doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        String jsonMsg = null;
        String baseUrl =
                "http://api.geonames.org/countryInfoJSON?"+
                        "formatted=true"+
                        "&lang="     + selectedLanguage +
                        "&country="  + selectedCountry +
                        "&username=demo"+
                        "&style=full";

        urlConnection = connect(baseUrl);
        jsonMsg = getDataAndCloseConnection(urlConnection);
        return null;
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

    private String getDataAndCloseConnection(HttpURLConnection urlConnection){
        String rvcMsg = null;
        BufferedReader reader = null;

        try {
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null){
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }
                if (buffer.length() != 0){
                    rvcMsg = buffer.toString();
                }
            }
        }
        catch (IOException e){
            Log.e("MainActivityFragment", "Error accesing stream", e);
        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                } catch (IOException e) {
                    Log.e("MainActivityFragment", "Error closing stream", e);
                }
            }
        }

        Log.i("MainActivityFragment", rvcMsg);
        return rvcMsg;
    }
}
}
