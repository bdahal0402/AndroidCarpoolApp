package com.example.bdtermproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class HomePage extends FragmentActivity {

    public static Context context;
    String dataUrl = "http://136.32.51.159/carpool/activeOffering.php";
    ArrayAdapter<String> adapter;
    ListView listView;
    static ArrayList<String> userData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        context = this;
        Button settings = findViewById(R.id.userSettingBtn);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, UserSettings.class);
                startActivity(i);
                finish();
            }
        });

        listView = findViewById(R.id.users_list_view);

        if (UserSettings.userRideOption.toLowerCase().equals("looking"))
            dataUrl = "http://136.32.51.159/carpool/activeOffering.php";
        if (UserSettings.userRideOption.toLowerCase().equals("offering"))
            dataUrl = "http://136.32.51.159/carpool/activeLooking.php";

        userData.clear();

        StringRequest stringRequest = new StringRequest(dataUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray json = new JSONArray(response);
                    for(int i=0;i<json.length();i++){
                        JSONObject e = new JSONObject(json.getJSONObject(i).toString().replace("[","").replace("]",""));
                        String fullname = e.getString("FullName");
                        String destination = e.getString("Destination");
                        userData.add(fullname + "\nGoing to: " + destination);
                        setList();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);


    }

    public void setList(){

        UserList userListFragment = new UserList();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.usersList, userListFragment);
        transaction.commit();
    }

}

