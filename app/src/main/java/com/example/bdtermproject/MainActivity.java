package com.example.bdtermproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.map_key));

        PlacesClient placesClient = Places.createClient(this);

        Intent i = new Intent(MainActivity.this, Login.class);
        startActivity(i);
        finish();
    }

}
