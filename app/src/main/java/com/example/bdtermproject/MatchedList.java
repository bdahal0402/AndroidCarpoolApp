package com.example.bdtermproject;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MatchedList extends Fragment  {

    public static ListView lv;
    static boolean listEmpty;

    public static String matchedClickedFullName;
    public static String matchedClickedRideOption;
    public static String matchClickedDetails;
    public static ArrayList<String> matchClickRatings = new ArrayList<>();
    public static ArrayList<String> matchClickRatedUsers = new ArrayList<>();
    public static String matchClickedUsername;



    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        savedInstanceState = getArguments();
        if (savedInstanceState!=null)
        {}
        View v =  inflator.inflate(R.layout.trips, container,false);
        lv = v.findViewById(R.id.trips_list_view);

        if (UserSettings.matches.size() == 0) {
            listEmpty = true;
            UserSettings.matches.add("You have no matched trips.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.matchtext, R.id.textItem, UserSettings.matches);
        lv.setAdapter(adapter);
        if (listEmpty) {
            UserSettings.matches.clear();
            listEmpty = false;
            return v;
        }
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                MatchedList.matchClickRatedUsers.clear();
                MatchedList.matchClickRatings.clear();

                final String[] splitNames = UserSettings.matches.get(position).split(" - ");
                final String profileUsername = splitNames[0];

                JSONObject request = new JSONObject();
                try {
                    request.put("username", profileUsername);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                        (Request.Method.POST, "http://136.32.51.159/carpool/fullinfo.php", request, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject e) {

                                try {
                                    matchedClickedFullName = e.getString("FullName");
                                    matchedClickedRideOption = e.getString("UseStatus");
                                    matchClickedUsername = profileUsername;
                                    String email = e.getString("Email");
                                    String activity = e.getString("Status");
                                    String location = e.getString("Location");
                                    String destination = e.getString("Destination");
                                    if (matchedClickedRideOption.toLowerCase().equals("looking")){
                                        if (activity.toLowerCase().equals("active"))
                                            matchClickedDetails = "Email Address: " + email.trim() +"\nActivity Status: " + activity.toUpperCase() +
                                                    "\nRide Option: LOOKING\nPickup Location: " + location + "\nDestination: " + destination + "\n";
                                        else
                                            matchClickedDetails = "Email Address: " + email.trim() +"\nActivity Status: " + activity.toUpperCase() + "\n";
                                    }
                                    if (matchedClickedRideOption.toLowerCase().equals("offering")){
                                        if (activity.toLowerCase().equals("active"))
                                            matchClickedDetails = "Email Address: " + email.trim() +"\nActivity Status: " + activity.toUpperCase() +
                                                    "\nRide Option: OFFERING\nDestination: " + destination + "\n";
                                        else
                                            matchClickedDetails = "Email Address: " + email.trim() +"\nActivity Status: " + activity.toUpperCase() + "\n";
                                    }
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
                                                matchClickRatings.add("Rating: " + val.getString("stars") + "/5 \n" + "Review: " + val.getString("rating_message"));
                                                matchClickRatedUsers.add(val.getString("user"));
                                            }


                                        } catch (JSONException E) {
                                            E.printStackTrace();
                                        }
                                    }
                                    else{
                                        matchClickRatings.add("This user doesn't have any reviews.");
                                    }
                                    Trips.viewingSelfProfile = false;
                                    setList();

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

                RequestCall.getInstance(getContext()).addToRequestQueue(jsArrayRequest);
            }
        });

        return v;
    }

    public void setList(){
        UserProfile userProfile = new UserProfile();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            transaction.replace(R.id.userProfileView, userProfile);
        else
            transaction.replace(R.id.matchedUserList, userProfile);
        transaction.commit();
    }


}
