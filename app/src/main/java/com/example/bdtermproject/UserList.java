package com.example.bdtermproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;

public class UserList extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        savedInstanceState = getArguments();
        if (savedInstanceState!=null)
        {}
        View v =  inflator.inflate(R.layout.users_listview, container,false);
        ListView listView = v.findViewById(R.id.users_list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.textcenter, R.id.textItem, HomePage.userData);
        listView.setAdapter(adapter);

        return v;
    }



}