package com.example.bdtermproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
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
    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_DESTINATION = "Destination";
    private static final String KEY_EMPTY = "";
    private EditText editUsername;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editEmail;
    private EditText editFirstName;
    private EditText editLastName;
    private AutoCompleteTextView editAddress;
    private AutoCompleteTextView editDestination;
    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String signUpAddress;
    private String signUpDestination;



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
        editAddress = findViewById(R.id.signUpAddress);
        editDestination = findViewById(R.id.signUpDestination);



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
                signUpAddress = editAddress.getText().toString().trim();
                signUpDestination = editDestination.getText().toString().trim();
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
            editAddress.setError("Please fill out your address.");
            editAddress.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(signUpDestination)){
            editDestination.setError("Please fill out your destination.");
            editDestination.requestFocus();
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