package com.example.bdtermproject;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    public static String userClickedName;
    public static String userClickedDescription;
    public static String userClickedAddress;
    public static String userClickedDestination;


    public MapFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

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

        CameraPosition pos = CameraPosition.builder().target(new LatLng(40.689247, -74.044502)).zoom(16).bearing(0).tilt(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

        Button matchBtn = getView().findViewById(R.id.matchBtn);
        if (UserSettings.userRideOption.toLowerCase().equals("looking")) {
            matchBtn.setText("Request ride");

            googleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247, -74.044502))
                    .title(userClickedName))
                    .setSnippet("Destination");

        }
        else if (UserSettings.userRideOption.toLowerCase().equals("offering")) {
            matchBtn.setText("Offer ride");
            googleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247, -74.044502))
                    .title(userClickedName))
                    .setSnippet("Pick up location");

            googleMap.addMarker(new MarkerOptions().position(new LatLng(50.689247, -74.044502))
                    .title(userClickedName))
                    .setSnippet("Destination");

        }

    }

}
