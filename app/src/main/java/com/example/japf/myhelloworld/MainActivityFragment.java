package com.example.japf.myhelloworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

    public MainActivityFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            Toast.makeText(getContext(), "Refreshing from fragment", Toast.LENGTH_LONG).show();
            return true;
        }
        /*
        else if (id == R.id.action_settings){
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Fake data to populate the list of contacts
        String[] names ={
                "John", "Mary", "Joe", "Jane", "Elena","Maud", "Phil", "Teo", "Nemo"
        };

        Integer [] images = {
                R.drawable.face_1, R.drawable.face_2, R.drawable.face_3,
                R.drawable.face_4, R.drawable.face_5, R.drawable.face_6,
                R.drawable.face_7, R.drawable.face_8, R.drawable.face_9
        };

        contactsList = new ArrayList<String>(Arrays.asList(names));
        contactsListAdapter =
                // new ArrayAdapter<String>(
                new MyListAdapter(
                        getActivity(), names, images);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.contact_list_view);
        listView.setAdapter(contactsListAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*
                        String name = ((TextView) view.findViewById(R.id.contact_name)).getText().toString();
                        Toast.makeText(getContext(), name, Toast.LENGTH_LONG).show();
                        */
                        startActivity(new Intent(getActivity(), CountryActivity.class));
                    }
                }
        );

        return rootView;
    }
}
