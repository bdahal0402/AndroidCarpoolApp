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

    final String API_ENDPOINT = "https://coolendpointthatworks.com/carpool";
    final String OFFERING_URL = API_ENDPOINT + "/activeOffering";
    final String LOOKING_URL = API_ENDPOINT + "/activeLooking";
    final String LOGIN_URL = API_ENDPOINT + "/login";


    String dataUrl = OFFERING_URL;

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
                (Request.Method.POST, LOGIN_URL, request, new Response.Listener<JSONObject>() {
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


                                boolean success = true;
                                if (userRequests.contains("user")) {
                                    try {
                                        JSONArray json = new JSONArray(userRequests);
                                        for (int i = 0; i < json.length(); i++) {
                                            JSONObject e = new JSONObject(json.getJSONObject(i).toString());
                                            UserSettings.requests.add(e.getString("user")+ " - " + e.getString("fullname"));
                                        }
                                    } catch (JSONException E) {
                                        success = false;
                                    }
                                }

                                if (success && userMatches.contains("user")) {
                                    try {
                                        JSONArray json = new JSONArray(userMatches);
                                        for (int i = 0; i < json.length(); i++) {
                                            JSONObject e = new JSONObject(json.getJSONObject(i).toString());
                                            UserSettings.matches.add(e.getString("user")+ " - " + e.getString("fullname"));
                                        }
                                    } catch (JSONException E) {
                                        success = false;
                                    }
                                }

                                if (success && userSentRequests.contains("user")) {
                                    try {
                                        JSONArray json = new JSONArray(userSentRequests);
                                        for (int i = 0; i < json.length(); i++) {
                                            JSONObject e = new JSONObject(json.getJSONObject(i).toString());
                                            UserSettings.sentRequests.add(e.getString("user")+ " - " + e.getString("fullname"));
                                        }
                                    } catch (JSONException E) {
                                        success = false;
                                    }
                                }

                            }else{
                                success = false;
                            }
                        } catch (JSONException e) {
                            success = false;
                        }

                        if (!success) {
                            Toast.makeText(getApplicationContext(),
                                        "Error fetching users list. Please try again soon.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        Toast.makeText(getApplicationContext(),
                                        "Error fetching users list. Please try again soon.", Toast.LENGTH_SHORT).show();

                    }
                });

        RequestCall.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);


        updateList();
    }

    void updateList(){
        listView = findViewById(R.id.users_list_view);

        if (UserSettings.userRideOption.toLowerCase().equals("looking"))
            dataUrl = LOOKING_URL;
        if (UserSettings.userRideOption.toLowerCase().equals("offering"))
            dataUrl = OFFERING_URL;

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
                        String averageRating = e.getString("AverageRating");
                        if (!averageRating.toLowerCase().contains("no"))
                            averageRating = String.format("%.2f", Double.parseDouble(e.getString("AverageRating")));
                        LatLng addressLatLng = new LatLng(Double.parseDouble(e.getString("AddressLatitude")), Double.parseDouble(e.getString("AddressLongitude")));
                        LatLng userDestLatLng = new LatLng(Double.parseDouble(e.getString("DestLatitude")), Double.parseDouble(e.getString("DestLongitude")));
                        String usn = e.getString("Username");
                        if (averageRating.length() < 5)
                            averageRating += "/5.00";
                        if (rideOption.equals("looking"))
                            userData.add(fullname + " - Looking for a ride\nAverage rating: " + averageRating + "\nPickup: " + address + "\nDestination: " + destination);
                        if (rideOption.equals("offering"))
                            userData.add(fullname + " - Offering a ride\nAverage rating: " + averageRating+ "\nDestination: " + destination);

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
                Toast.makeText(getApplicationContext(),
                                        "Error updating users list. Please try again soon.", Toast.LENGTH_SHORT).show();
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

