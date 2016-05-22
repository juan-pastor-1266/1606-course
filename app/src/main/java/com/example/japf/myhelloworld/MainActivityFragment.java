package com.example.japf.myhelloworld;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    List<String> contactsList;
    ArrayAdapter<String> contactsListAdapter;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Fake data to populate the list of contacts
        String[] names ={
                "John",
                "Mary",
                "Joaquin",
                "Jane",
                "Elena",
                "Maurice",
                "Filemon",
                "Teofila"
        };

        contactsList = new ArrayList<String>(Arrays.asList(names));
        contactsListAdapter =
                new ArrayAdapter<String>(
                        getActivity(),        // The current context
                        R.layout.my_list_row, // layout id
                        R.id.contact_name,    // textview to populate.
                        names);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(contactsListAdapter);

        return rootView;
    }
}
