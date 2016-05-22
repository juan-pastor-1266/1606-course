package com.example.japf.myhelloworld;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by japf on 22/05/2016.
 */
public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    public MyListAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.my_list_row, itemname);
        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.my_list_row, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.contact_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.contact_icon);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        return rowView;
    };
}
