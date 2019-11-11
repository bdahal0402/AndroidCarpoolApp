package com.example.bdtermproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "FullName";
    private static final String KEY_USERNAME = "Username";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_EMPTY = "";
    private EditText editUsername;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editEmail;
    private EditText editFirstName;
    private EditText editLastName;
    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String register_url = "http://136.32.51.159/carpool/register.php";

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
        Intent i = new Intent(getApplicationContext(), HomePage.class);
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
            editFirstName.setError("Full Name cannot be empty");
            editFirstName.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(lastName)) {
            editLastName.setError("Last Name cannot be empty");
            editLastName.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(username)) {
            editUsername.setError("Username cannot be empty");
            editUsername.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(password)) {
            editPassword.setError("Password cannot be empty");
            editPassword.requestFocus();
            return false;
        }

        if (KEY_EMPTY.equals(email)){
            editEmail.setError("Email cannot be empty");
            editEmail.requestFocus();
            return false;
        }

        if (KEY_EMPTY.equals(confirmPassword)) {
            editConfirmPassword.setError("Confirm Password cannot be empty");
            editConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            editConfirmPassword.setError("Password and confirm password does not match");
            editConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }
}