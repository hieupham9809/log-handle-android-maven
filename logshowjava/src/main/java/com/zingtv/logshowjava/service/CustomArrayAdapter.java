package com.zingtv.logshowjava.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;


import java.util.Arrays;
import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter {
    Context context;
    String[] priorites;
    private OnSelectItemListener listener;

    public void setOnSelectItemListener(OnSelectItemListener listener){
        this.listener = listener;
    }

    public CustomArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.priorites = objects;
    }

    public int getPosition(String item){
        List priorityList = Arrays.asList(priorites);
        return priorityList.indexOf(item);
    }
    @Override
    public View getDropDownView(final int position, View convertView, final ViewGroup parent) {
        TextView v = (TextView) getView(position, convertView, parent).findViewById(android.R.id.text1);




        v.setText(getItem(position));
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onSelected(getItem(position));

                }

            }
        });
        return v;
    }

    @Override
    public int getCount() {
        return priorites.length;
    }

    @Override
    public String getItem(int position) {
        return priorites[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null );

        }
        return convertView;
    }

    interface OnSelectItemListener{
        void onSelected (String priority);
    }
}
