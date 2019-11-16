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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    public static String userClickedName;
    public static String userClickedDescription;
    public static String userClickedAddress;
    public static String userClickedDestination;
    public static LatLng userClickedAddressLatLng;
    public static LatLng userClickedDestLatLng;



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

        LatLng cameraMidpoint = userClickedDestLatLng;
        float cameraBearing = 0;

        Button matchBtn = getView().findViewById(R.id.matchBtn);
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
