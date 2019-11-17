package com.example.bdtermproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    public static String loggedInUser;
    public static String loggedInUserFullName;

    public static String  signUpSuccess;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "Username";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_EMPTY = "";
    private EditText editUsername;
    private EditText editPassword;
    private String username;
    private String password;
    private ProgressDialog pDialog;
    private String login_url = "http://136.32.51.159/carpool/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if (signUpSuccess == "yes") {
            Toast.makeText(this, "Signup successful, please login to continue!", Toast.LENGTH_SHORT).show();
            signUpSuccess = "no";
        }
        editUsername = findViewById(R.id.loginUsername);
        editPassword = findViewById(R.id.loginPassword);


        Button register = findViewById(R.id.signUpButton);
        Button login = findViewById(R.id.loginButton);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, SignUp.class);
                startActivity(i);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editUsername.getText().toString().toLowerCase().trim();
                password = editPassword.getText().toString().trim();
                if (validateInputs()) {
                    login();
                }
            }
        });
    }


    private void loadDashboard() {
        loggedInUser = username;
        Intent i = new Intent(getApplicationContext(), HomePage.class);
        startActivity(i);
        finish();

    }

    private void displayLoader() {
        pDialog = new ProgressDialog(Login.this);
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void login() {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            request.put(KEY_USERNAME, username);
            request.put(KEY_PASSWORD, password);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            if (response.getInt(KEY_STATUS) == 0) {
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
                                loggedInUserFullName = response.getString("FullName");

                                if (userRequests.contains("user")) {
                                    if (userRequests.endsWith(",")) {
                                        userRequests = removeLastChar(userRequests);
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
                                        userMatches = removeLastChar(userMatches);
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
                                        userSentRequests = removeLastChar(userSentRequests);
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



                                loadDashboard();

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
                        pDialog.dismiss();

                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        RequestCall.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    private boolean validateInputs() {
        if(KEY_EMPTY.equals(username)){
            editUsername.setError("Please fill out the username!");
            editPassword.requestFocus();
            return false;
        }
        if(KEY_EMPTY.equals(password)){
            editPassword.setError("Please fill out the password!");
            editPassword.requestFocus();
            return false;
        }
        return true;
    }
    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }
}