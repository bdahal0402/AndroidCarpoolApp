package com.example.bdtermproject;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        context = this;
        Button settings = findViewById(R.id.userSettingBtn);
        Button refresh = findViewById(R.id.refreshBtn);


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
                updateList();
            }
        });

        updateList();

    }

    void updateList(){
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
                        String address = e.getString("Location");
                        String destination = e.getString("Destination");
                        if (e.getString("UseStatus").toLowerCase().equals("looking"))
                            userData.add(fullname + " - Looking for a ride\nPickup: " + address + "\nDestination: " + destination);
                        if (e.getString("UseStatus").toLowerCase().equals("offering"))
                            userData.add(fullname + " - Offering a ride\nDestination: " + destination);
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

