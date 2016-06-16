package com.example.japf.myhelloworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class CountryActivityFragment extends Fragment {

    public CountryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_country, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String countryData = intent.getStringExtra(Intent.EXTRA_TEXT);

            // Show data according to settings.
            String dataToShow = new String();
            dataToShow += Utils.getFieldForCountryJSON(countryData, "countryName") + "\n";
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            //String prefs = sharedPref.contains("pref_show_capital")?"yes":"no";
            //dataToShow += ("\n" + "******" + prefs + "\n\n");

            if (sharedPref.getBoolean("pref_show_capital", false)) {
                dataToShow += ("Capital: " + Utils.getFieldForCountryJSON(countryData, "capital") + "\n");
            }
            if (sharedPref.getBoolean("pref_show_population", false)) {
                dataToShow += ("Population: " + Utils.getFieldForCountryJSON(countryData, "population") + "\n");
            }
            if (sharedPref.getBoolean("pref_show_languages", false)) {
                dataToShow += ("Languages: " + Utils.getFieldForCountryJSON(countryData, "languages") + "\n");
            }


            ((TextView) rootView.findViewById(R.id.country_info))
                    .setText(dataToShow);
        }

        return rootView;
    }
}
