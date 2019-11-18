package com.example.bdtermproject;


import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class HomePage extends FragmentActivity {

    public static Context context;
    String dataUrl = "http://136.32.51.159/carpool/activeOffering.php";
    ArrayAdapter<String> adapter;
    ListView listView;
    static ArrayList<String> userData = new ArrayList<>();
    static ArrayList<String> userFullName = new ArrayList<>();
    static ArrayList<String> userUsername = new ArrayList<>();
    static ArrayList<String> userLocation = new ArrayList<>();
    static ArrayList<String> userDestination = new ArrayList<>();
    static ArrayList<String> userRideOption = new ArrayList<>();
    static ArrayList<LatLng> userAddressLatLng = new ArrayList<>();
    static ArrayList<LatLng> userDestinationLatLng = new ArrayList<>();

    ProgressDialog pDialog;
    private void displayLoader() {
        pDialog = new ProgressDialog(HomePage.this);
        pDialog.setMessage("Refreshing....");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        context = this;


        Button settings = findViewById(R.id.userSettingBtn);
        final Button refresh = findViewById(R.id.refreshBtn);
        Button logout = findViewById(R.id.logoutButton);
        Button trips = findViewById(R.id.matchedUsers);


        trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, Trips.class);
                startActivity(i);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getApplicationContext(), Login.class);
                ActivityCompat.finishAffinity(HomePage.this);
                startActivity(intent);*/

                PackageManager packageManager = context.getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                context.startActivity(mainIntent);
                Runtime.getRuntime().exit(0);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, UserSettings.class);
                startActivity(i);
                finish();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshClick();
            }
        });

        updateList();


    }

    public void refreshClick(){
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            request.put("Username", Login.loggedInUser);
            request.put("Password", Login.refreshKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, "http://136.32.51.159/carpool/login.php", request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            if (response.getInt("status") == 0) {
                                UserSettings.matches.clear();
                                UserSettings.requests.clear();
                                UserSettings.sentRequests.clear();


                                UserSettings.userRideOption = response.getString("rideOption");
                                UserSettings.userActivityStatus = response.getString("activityStatus");
                                UserSettings.userStartAddress = response.getString("address");
                                UserSettings.userDestination = response.getString("destination");
                                UserSettings.addressLatitude = response.getString("startLat");
                                UserSettings.addressLongitude = response.getString("startLong");
                                UserSettings.destLatitude = response.getString("destLat");
                                UserSettings.destLongitude = response.getString("destLong");
                                String userRequests = response.getString("requestReceived");
                                String userMatches = response.getString("matches");
                                String userSentRequests = response.getString("requestSent");
                                Login.loggedInUserFullName = response.getString("FullName");

                                if (userRequests.contains("user")) {
                                    if (userRequests.endsWith(",")) {
                                        userRequests = Login.removeLastChar(userRequests);
                                        userRequests += "]";
                                    }

                                    try {
                                        JSONArray json = new JSONArray(userRequests);
                                        for (int i = 0; i < json.length(); i++) {
                                            JSONObject e = new JSONObject(json.getJSONObject(i).toString());
                                            UserSettings.requests.add(e.getString("user")+ " - " + e.getString("fullname"));
                                        }
                                    } catch (JSONException E) {
                                        E.printStackTrace();
                                    }
                                }

                                if (userMatches.contains("user")) {
                                    if (userMatches.endsWith(",")) {
                                        userMatches = Login.removeLastChar(userMatches);
                                        userMatches += "]";
                                    }

                                    try {
                                        JSONArray json = new JSONArray(userMatches);
                                        for (int i = 0; i < json.length(); i++) {
                                            JSONObject e = new JSONObject(json.getJSONObject(i).toString());
                                            UserSettings.matches.add(e.getString("user")+ " - " + e.getString("fullname"));
                                        }
                                    } catch (JSONException E) {
                                        E.printStackTrace();
                                    }
                                }

                                if (userSentRequests.contains("user")) {
                                    if (userSentRequests.endsWith(",")) {
                                        userSentRequests = Login.removeLastChar(userSentRequests);
                                        userSentRequests += "]";
                                    }

                                    try {
                                        JSONArray json = new JSONArray(userSentRequests);
                                        for (int i = 0; i < json.length(); i++) {
                                            JSONObject e = new JSONObject(json.getJSONObject(i).toString());
                                            UserSettings.sentRequests.add(e.getString("user")+ " - " + e.getString("fullname"));
                                        }
                                    } catch (JSONException E) {
                                        E.printStackTrace();
                                    }
                                }

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        "Error refreshing!", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        Toast.makeText(getApplicationContext(),
                                "Error refreshing!", Toast.LENGTH_SHORT).show();

                    }
                });

        RequestCall.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);


        updateList();
    }

    void updateList(){
        listView = findViewById(R.id.users_list_view);

        if (UserSettings.userRideOption.toLowerCase().equals("looking"))
            dataUrl = "http://136.32.51.159/carpool/activeOffering.php";
        if (UserSettings.userRideOption.toLowerCase().equals("offering"))
            dataUrl = "http://136.32.51.159/carpool/activeLooking.php";

        userData.clear();
        userDestination.clear();
        userLocation.clear();
        userDestination.clear();
        userFullName.clear();
        userUsername.clear();

        StringRequest stringRequest = new StringRequest(dataUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray json = new JSONArray(response);
                    for(int i=0;i<json.length();i++){
                        JSONObject e = new JSONObject(json.getJSONObject(i).toString().replace("[","").replace("]",""));
                        String fullname = e.getString("FullName");
                        String address = e.getString("Location");
                        String destination = e.getString("Destination");
                        String rideOption = e.getString("UseStatus").toLowerCase();
                        LatLng addressLatLng = new LatLng(Double.parseDouble(e.getString("AddressLatitude")), Double.parseDouble(e.getString("AddressLongitude")));
                        LatLng userDestLatLng = new LatLng(Double.parseDouble(e.getString("DestLatitude")), Double.parseDouble(e.getString("DestLongitude")));
                        String usn = e.getString("Username");

                        if (rideOption.equals("looking"))
                            userData.add(fullname + " - Looking for a ride\nPickup: " + address + "\nDestination: " + destination);
                        if (rideOption.equals("offering"))
                            userData.add(fullname + " - Offering a ride\nDestination: " + destination);

                        userUsername.add(usn);
                        userFullName.add(fullname);
                        userLocation.add(address);
                        userDestination.add(destination);
                        userRideOption.add(rideOption);
                        userAddressLatLng.add(addressLatLng);
                        userDestinationLatLng.add(userDestLatLng);
                        UserList.noUsers = false;

                    }

                    setList();

                } catch (JSONException e) {
                    e.printStackTrace();
                    HomePage.userData.add("Your ride options do not match any other users. Please check again later.");
                    UserList.noUsers = true;
                    setList();
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

