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


import java.util.ArrayList;

public class UserList extends Fragment  {

    public static ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        savedInstanceState = getArguments();
        if (savedInstanceState!=null)
        {}
        View v =  inflator.inflate(R.layout.users_listview, container,false);
        lv = v.findViewById(R.id.users_list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.textcenter, R.id.textItem, HomePage.userData);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                MapFragment.userClickedName = HomePage.userFullName.get(position);
                MapFragment.userClickedAddress = HomePage.userLocation.get(position);
                MapFragment.userClickedDestination = HomePage.userDestination.get(position);
                if (HomePage.userRideOption.get(position).toLowerCase().equals("offering"))
                    MapFragment.userClickedDescription = "Offering - Destination";
                MapFragment mapFragment = new MapFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                    transaction.replace(R.id.landMapView, mapFragment);
                else
                    transaction.replace(R.id.usersList, mapFragment);
                transaction.commit();




            }
        });

        return v;
    }


}
