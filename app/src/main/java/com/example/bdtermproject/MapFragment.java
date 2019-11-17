package com.example.bdtermproject;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    public static String userClickedUsername;
    public static String userClickedName;
    public static String userClickedDescription;
    public static String userClickedAddress;
    public static String userClickedDestination;
    public static LatLng userClickedAddressLatLng;
    public static LatLng userClickedDestLatLng;

    public static boolean alreadyRequested;

    public MapFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.map_fragment, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mMapView = mView.findViewById(R.id.mapV);
        if (mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(HomePage.context);
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng cameraMidpoint = userClickedDestLatLng;
        float cameraBearing = 0;

        final Button matchBtn = getView().findViewById(R.id.matchBtn);




        if (UserSettings.userRideOption.toLowerCase().equals("looking")) {
            matchBtn.setText("Request ride");




            googleMap.addMarker(new MarkerOptions().position(userClickedDestLatLng)
                    .title(userClickedName))
                    .setSnippet("Destination");

        }
        else if (UserSettings.userRideOption.toLowerCase().equals("offering")) {
            matchBtn.setText("Offer ride");

            googleMap.addMarker(new MarkerOptions().position(userClickedAddressLatLng)
                    .title(userClickedName))
                    .setSnippet("Pick up location");

            googleMap.addMarker(new MarkerOptions().position(userClickedDestLatLng)
                    .title(userClickedName))
                    .setSnippet("Destination");

            cameraMidpoint = midPoint(userClickedAddressLatLng.latitude, userClickedAddressLatLng.longitude, userClickedDestLatLng.latitude, userClickedDestLatLng.longitude);
            cameraBearing = angleBetweenCoordinate(userClickedAddressLatLng.latitude, userClickedAddressLatLng.longitude, userClickedDestLatLng.latitude, userClickedDestLatLng.longitude);

        }

        googleMap.setMyLocationEnabled(true);
        CameraPosition pos = CameraPosition.builder().target(cameraMidpoint).zoom(10).bearing(cameraBearing).tilt(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

        alreadyRequested = false;
        for (int i =0; i< UserSettings.sentRequests.size(); i++){
            if (UserSettings.requests.get(i).contains(userClickedUsername)) {
                alreadyRequested = true;
                if (UserSettings.userRideOption.toLowerCase().equals("looking"))
                    matchBtn.setText("Cancel request");
                else
                    matchBtn.setText("Cancel offer");
            }
        }

        matchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userDetailsURL = "http://136.32.51.159/carpool/sendRequest.php";




                if(alreadyRequested){

                }

                if(UserSettings.userActivityStatus.toLowerCase().equals("inactive")){
                    TastyToast.makeText(getContext(), "You must set your activity status to active first.", TastyToast.LENGTH_SHORT, TastyToast.CONFUSING);
                    return;
                }

                JSONObject request = new JSONObject();
                try {
                    request.put("user1", Login.loggedInUser);
                    request.put("user2", userClickedUsername);
                    request.put("user1Full", Login.loggedInUserFullName);
                    request.put("user2Full", userClickedName);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                        (Request.Method.POST, userDetailsURL, request, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getInt("status") == 0) {
                                        TastyToast.makeText(getContext(),
                                            response.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                        UserSettings.sentRequests.add(userClickedUsername + " - " + userClickedName);
                                        if (UserSettings.userRideOption.toLowerCase().equals("looking"))
                                            matchBtn.setText("Cancel request");
                                        else
                                            matchBtn.setText("Cancel offer");

                                    }else{
                                        TastyToast.makeText(getContext(),
                                                response.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                                TastyToast.makeText(getContext(),
                                        "Something went wrong updating the record!", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                            }
                        });
                RequestCall.getInstance(getContext()).addToRequestQueue(jsArrayRequest);
            }
        });

    }

    private LatLng midPoint(double lat1, double long1, double lat2,double long2)
    {
        return new LatLng((lat1+lat2)/2, (long1+long2)/2);
    }

    private float angleBetweenCoordinate(double lat1, double long1, double lat2,
                                         double long2) {

        double xDiff = lat2 - lat2;
        double yDiff = long2 - long1;
        return (float)(Math.atan2(yDiff, xDiff) * 180.0 / Math.PI);
    }


}
