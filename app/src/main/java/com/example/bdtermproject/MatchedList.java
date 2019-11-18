package com.example.bdtermproject;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MatchedList extends Fragment  {

    public static ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        savedInstanceState = getArguments();
        if (savedInstanceState!=null)
        {}
        View v =  inflator.inflate(R.layout.trips, container,false);
        lv = v.findViewById(R.id.trips_list_view);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.matchtext, R.id.textItem, UserSettings.matches);
        lv.setAdapter(adapter);
        if (UserSettings.matches.size() == 0)
            return v;
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {




            }
        });

        return v;
    }


}
