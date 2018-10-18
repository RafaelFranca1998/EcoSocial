package com.example.rafael_cruz.prototipo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectOnMapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, LocationListener {

    private Context context;
    private int REQUEST_LOCATION;
    private MapView mMapView;
    private GoogleMap googleMap;
    private static LatLng position;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        // TODO Auto-generated method stub
                        // Here your code
                        Toast.makeText(context, "Dragging Start",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                        // TODO Auto-generated method stub
                        // Toast.makeText(MainActivity.this, "Dragging",
                        // Toast.LENGTH_SHORT).show();
                        System.out.println("Draagging");
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        // TODO Auto-generated method stub
                        position = marker.getPosition(); //
                        Toast.makeText(
                                context,
                                "Lat " + position.latitude + " "
                                        + "Long " + position.longitude,
                                Toast.LENGTH_LONG).show();
//                        AddEventActivity activity1 = new AddEventActivity();
//                        activity1.setLocalizacao(position);
                    }
                });

                // For showing a move to my location button

                // For dropping a marker at a point on the Map


                LatLng salvador = new LatLng(-12.999998, -38.493746);


                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(salvador).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                LatLng base = new LatLng(-12.999998, -38.493746);
                googleMap.addMarker(new MarkerOptions().position(base).title("Clique e arraste")
                        .snippet("Local: Sua casa \n Horario: o dia todo ").draggable(true));
            }
        });



        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

        // For showing a move to my location button

        // For dropping a marker at a point on the Map
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( //Method of Fragment
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION
            );
        } else {

        }


        mMap.setMyLocationEnabled(true);
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean onMarkerClick(final Marker marker) {

        if (marker.equals(googleMap)) {
            // handle click here
            // map.getMyLocation();
            System.out.println("Clicked");
            double lat = googleMap.getMyLocation().getLatitude();
            System.out.println("Lat" + lat);
            Toast.makeText(context,
                    "Current location " + googleMap.getMyLocation().getLatitude(),
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public static LatLng getLatLong(){
        return position;
    }



}
