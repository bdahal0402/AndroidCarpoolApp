package com.example.bdtermproject;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Trips extends FragmentActivity {
    ProgressDialog pDialog;
    public static boolean viewingSelfProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_trips);

        Button homePage = findViewById(R.id.homePage);

        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Trips.this, HomePage.class);
                startActivity(i);
                finish();
            }
        });



        Button settings = findViewById(R.id.userSettingBtn);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Trips.this, UserSettings.class);
                startActivity(i);
                finish();
            }
        });

        Button refresh = findViewById(R.id.refreshBtn);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshClick();
            }
        });

        Button myReviews = findViewById(R.id.myreviews);
        myReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewingSelfProfile = true;
                MatchedList.matchClickRatedUsers.clear();
                MatchedList.matchClickRatings.clear();
                JSONObject request = new JSONObject();
                try {
                    request.put("username", Login.loggedInUser);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                        (Request.Method.POST, "http://136.32.51.159/carpool/fullinfo.php", request, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject e) {

                                try {
                                    MatchedList.matchedClickedFullName = e.getString("FullName");
                                    MatchedList.matchedClickedRideOption = e.getString("UseStatus");
                                    String email = e.getString("Email");
                                    String activity = e.getString("Status");
                                    String location = e.getString("Location");
                                    String destination = e.getString("Destination");

                                    MatchedList.matchClickedDetails = "Email Address: " + email.trim() +"\nActivity Status: " + activity.toUpperCase() +
                                            "\nRide Option: " + MatchedList.matchedClickedRideOption.toUpperCase() + "\nPickup Location: " + location + "\nDestination: " + destination + "\n";

                                    String reviews = e.getString("ratings");
                                    if (reviews.contains("user")) {
                                        if (reviews.endsWith(",")) {
                                            reviews = Login.removeLastChar(reviews);
                                            reviews += "]";
                                        }

                                        try {
                                            JSONArray json = new JSONArray(reviews);
                                            for (int i = 0; i < json.length(); i++) {
                                                JSONObject val = new JSONObject(json.getJSONObject(i).toString());
                                                MatchedList.matchClickRatings.add("Rating: " + val.getString("stars") + "/5 \n" + "Review: " + val.getString("rating_message"));
                                                MatchedList.matchClickRatedUsers.add(val.getString("user"));
                                            }



                                        } catch (JSONException E) {
                                            E.printStackTrace();
                                        }
                                    }
                                    else{
                                        MatchedList.matchClickRatings.add("You do have any reviews.");
                                    }
                                    UserProfile profile = new UserProfile();
                                    FragmentManager fm = getSupportFragmentManager();
                                    FragmentTransaction transaction = fm.beginTransaction();
                                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                                        transaction.replace(R.id.matchedUserList, profile);
                                    else
                                        transaction.replace(R.id.userProfileView, profile);
                                    transaction.commit();

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.print("error");
                            }
                        });

                RequestCall.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);


            }
        });

        setList();

    }

    private void displayLoader() {
        pDialog = new ProgressDialog(Trips.this);
        pDialog.setMessage("Refreshing....");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
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
                                MatchedList.matchClickRatedUsers.clear();
                                MatchedList.matchClickRatings.clear();

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


        setList();
    }


    public void setList(){


        MatchedList matchedList = new MatchedList();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.matchedUserList, matchedList);
        transaction.commit();


    }


}
