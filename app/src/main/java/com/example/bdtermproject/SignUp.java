package com.example.bdtermproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.lang.reflect.Array;
import java.util.Arrays;


public class SignUp extends AppCompatActivity {


    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "FullName";
    private static final String KEY_USERNAME = "Username";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_DESTINATION = "Destination";
    private static final String KEY_STARTLAT = "StartLat";
    private static final String KEY_STARTLONG = "StartLong";
    private static final String KEY_DESTLAT = "DestLat";
    private static final String KEY_DESTLONG = "DestLong";


    private static final String KEY_EMPTY = "";
    private EditText editUsername;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editEmail;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText startAddress;
    private EditText destAddress;

    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String signUpAddress;
    private String signUpDestination;
    private String addressLatitude;
    private String addressLongitude;
    private String destLatitude;
    private String destLongitude;



    private String register_url = "http://136.32.51.159/carpool/register.php";

    PlacesClient placesClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        editUsername = findViewById(R.id.signUpUsername);
        editPassword = findViewById(R.id.signUpPassword);
        editConfirmPassword = findViewById(R.id.confirmPassword);
        editEmail = findViewById(R.id.signUpEmail);
        editFirstName = findViewById(R.id.signUpFirstName);
        editLastName = findViewById(R.id.signUpLastName);
        destAddress = findViewById(R.id.destVal);
        startAddress = findViewById(R.id.addressVal);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.map_key));
        }
        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentAddress);
        final AutocompleteSupportFragment autocompleteSupportFragmentDest = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentAddressDest);
        autocompleteSupportFragment.setHint("Address");
        autocompleteSupportFragmentDest.setHint("Destination");

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

            }
        });

        autocompleteSupportFragmentDest.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSupportFragmentDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();
                destAddress.setText(place.getName());
                destLatitude = String.valueOf(latLng.latitude);
                destLongitude = String.valueOf(latLng.longitude);

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


        Button login = findViewById(R.id.backToLogin);
        Button register = findViewById(R.id.signUpClick);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editUsername.getText().toString().toLowerCase().trim();
                password = editPassword.getText().toString().trim();
                email = editEmail.getText().toString().trim();
                confirmPassword = editConfirmPassword.getText().toString().trim();
                firstName = editFirstName.getText().toString().trim();
                lastName = editLastName.getText().toString().trim();
                signUpAddress = startAddress.getText().toString().trim();
                signUpDestination = destAddress.getText().toString().trim();
                if (validateInputs()) {
                    registerUser();
                }

            }
        });

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(SignUp.this, Login.class));
        finish();

    }

    private void loadDashboard() {

        Login.signUpSuccess = "yes";
        Intent i = new Intent(getApplicationContext(), Login.class);
        startActivity(i);
        finish();

    }

    private void registerUser() {
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, username);
            request.put(KEY_PASSWORD, password);
            request.put(KEY_FULL_NAME, firstName + " " + lastName);
            request.put(KEY_EMAIL, email);
            request.put(KEY_ADDRESS, signUpAddress);
            request.put(KEY_DESTINATION, signUpDestination);
            request.put(KEY_STARTLAT, addressLatitude);
            request.put(KEY_STARTLONG, addressLongitude);
            request.put(KEY_DESTLAT, destLatitude);
            request.put(KEY_DESTLONG, destLongitude);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(KEY_STATUS) == 0) {
                                loadDashboard();

                            }else if(response.getInt(KEY_STATUS) == 1){
                                editUsername.setError("Username already exists!");
                                editUsername.requestFocus();

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print(error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        RequestCall.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    private boolean validateInputs() {
        if (KEY_EMPTY.equals(firstName)) {
            editFirstName.setError("Please fill out the first name.");
            editFirstName.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(lastName)) {
            editLastName.setError("Please fill out the last name.");
            editLastName.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(username)) {
            editUsername.setError("Please fill out the username.");
            editUsername.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(password)) {
            editPassword.setError("Please fill out the password.");
            editPassword.requestFocus();
            return false;
        }

        if (KEY_EMPTY.equals(signUpAddress)){
            Toast.makeText(this, "Please fill out the address.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (KEY_EMPTY.equals(signUpDestination)){
            Toast.makeText(this, "Please fill out the destination.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (KEY_EMPTY.equals(email)){
            editEmail.setError("Please fill out the email.");
            editEmail.requestFocus();
            return false;
        }

        if (KEY_EMPTY.equals(confirmPassword)) {
            editConfirmPassword.setError("Please confirm your password.");
            editConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError("Password and confirm password does not match!");
            editConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }


}