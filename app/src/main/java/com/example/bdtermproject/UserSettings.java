package com.example.bdtermproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class UserSettings extends FragmentActivity {


    public static String userRideOption;
    public static String userActivityStatus;
    public static String userStartAddress;
    public static String userDestination;
    public static String addressLatitude;
    public static String addressLongitude;
    public static String destLatitude;
    public static String destLongitude;

    PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);

        final Button activityButton = findViewById(R.id.activityStatus);
        final Button rideButton = findViewById(R.id.rideOption);
        final Button cancelButton = findViewById(R.id.cancelUpdate);
        final Button updateButton = findViewById(R.id.updateUserInfo);
        final EditText startAddress = findViewById(R.id.startAddress);
        final EditText destination = findViewById(R.id.destination);

        startAddress.setText(userStartAddress);
        destination.setText(userDestination);


        if (userRideOption.toLowerCase().equals("looking"))
            rideButton.setText("RIDE OPTION: LOOKING");

        else if (userRideOption.toLowerCase().equals("offering"))
            rideButton.setText("RIDE OPTION: OFFERING");


        if (userActivityStatus.toLowerCase().equals("active"))
            activityButton.setText("ACTIVITY STATUS: ACTIVE");

        else if (userActivityStatus.toLowerCase().equals("inactive"))
            activityButton.setText("ACTIVITY STATUS: INACTIVE");



        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.map_key));
        }
        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentAddress);
        final AutocompleteSupportFragment autocompleteSupportFragmentDest = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentAddressDest);
        autocompleteSupportFragment.setHint("Update address here");
        autocompleteSupportFragmentDest.setHint("Update destination here");

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();
                startAddress.setText(place.getName());
                addressLatitude = String.valueOf(latLng.latitude);
                addressLongitude = String.valueOf(latLng.longitude);

            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getApplicationContext(), "Something went wrong getting the address", Toast.LENGTH_SHORT).show();
            }
        });

        autocompleteSupportFragmentDest.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSupportFragmentDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();
                destination.setText(place.getName());
                destLatitude = String.valueOf(latLng.latitude);
                destLongitude = String.valueOf(latLng.longitude);

            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getApplicationContext(), "Something went wrong getting the destination", Toast.LENGTH_SHORT).show();
            }
        });




        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustActivityButton();
            }
        });

        rideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustRideButton();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String userDetailsURL = "http://136.32.51.159/carpool/updateDetails.php";

                JSONObject request = new JSONObject();
                try {
                    request.put("Username", Login.loggedInUser);
                    request.put("Activity", userActivityStatus);
                    request.put("RideOption", userRideOption);
                    request.put("Address", startAddress.getText().toString());
                    request.put("Destination", destination.getText().toString());
                    request.put("startLat", addressLatitude);
                    request.put("startLong", addressLongitude);
                    request.put("destLat", destLatitude);
                    request.put("destLong", destLongitude);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                        (Request.Method.POST, userDetailsURL, request, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getInt("status") == 0) {

                                        Toast.makeText(UserSettings.this,
                                                response.getString("message"), Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(UserSettings.this,
                                                "Error! Please fill out the required text boxes!", Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(UserSettings.this,
                                        "Something went wrong updating the record!", Toast.LENGTH_SHORT).show();

                            }
                        });
                RequestCall.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HomePage.class);
                startActivity(i);
                finish();
            }
        });
    }

    void adjustRideButton(){
        final Button rideButton = findViewById(R.id.rideOption);

        if (userRideOption.toLowerCase().equals("looking")) {
            rideButton.setText("RIDE OPTION: OFFERING");
            userRideOption = "Offering";
        }

        else if (userRideOption.toLowerCase().equals("offering")) {
            rideButton.setText("RIDE OPTION: LOOKING");
            userRideOption = "Looking";
        }
    }

    void adjustActivityButton(){
        final Button activityButton = findViewById(R.id.activityStatus);


        if (userActivityStatus.toLowerCase().equals("active")) {
            activityButton.setText("ACTIVITY STATUS: INACTIVE");
            userActivityStatus = "inactive";
        }

        else if (userActivityStatus.toLowerCase().equals("inactive")) {
            activityButton.setText("ACTIVITY STATUS: ACTIVE");
            userActivityStatus = "active";
        }


    }



    public static void getUserDetails(String username, Context context) {
        String userDetailsURL = "http://136.32.51.159/carpool/userDetails.php";

        JSONObject request = new JSONObject();
        try {
            request.put("Username", username);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, userDetailsURL, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("status") == 0) {

                                userRideOption = response.getString("rideOption");
                                userActivityStatus = response.getString("activityStatus");
                                userStartAddress = response.getString("startAddress");
                                userDestination = response.getString("destination");
                            }else{
                                return ;

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
        RequestCall.getInstance(context).addToRequestQueue(jsArrayRequest);


    }
}
