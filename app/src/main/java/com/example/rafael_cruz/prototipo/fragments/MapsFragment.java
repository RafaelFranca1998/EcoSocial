package com.example.rafael_cruz.prototipo.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.activity.InfoEventoActivity;
import com.example.rafael_cruz.prototipo.activity.MainActivity;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.config.ItemEvento;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    int REQUEST_LOCATION;
    MapView mMapView;
    private static GoogleMap googleMap;
    List<Marker> markerList;
    List<Eventos> eventosList;
    LocationManager locationManager;
    Location mLocation;
    Double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        MainActivity.isInFragment = true;

        mMapView = rootView.findViewById(R.id.mapView);
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

                // For showing a move to my location button
                // For dropping a marker at a point on the Map


                markerList = new ArrayList<>();
                eventosList = new ArrayList<>();
                DatabaseReference databaseReference = DAO.getFireBase().child("events");
                final ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        markerList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Eventos eventos = data.getValue(Eventos.class);
                            BitmapDescriptor bitmapDescriptor;
                            if (eventos.getTipoEvento().equals("Animal Perdido")) {
                                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.dog_marker);
                            } else {
                                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.trash_marker);
                            }
                            LatLng latLng = new LatLng(eventos.getLat(), eventos.getLon());
                            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                                    .title(eventos.getTipoEvento()).snippet(eventos.getLocal()).icon(bitmapDescriptor));
                            eventos.setMarkerId(marker.getId());
                            eventosList.add(eventos);
                            markerList.add(marker);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                databaseReference.addValueEventListener(valueEventListener);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        for (int i = 0; i < eventosList.size();i++) {
                            String id = eventosList.get(i).getMarkerId();
                            String markerId = marker.getId();
                            if (markerId.equals(id)) {
                                Eventos eventos = eventosList.get(i);
                                InfoEventoActivity.isMap = true;
                                Intent intent =  new Intent(getActivity(), InfoEventoActivity.class);
                                intent.putExtra("eventos", eventos.getEventId());
                                startActivity(intent);
                            }
                        }
                        return false;
                    }
                });

                locationManager = MainActivity.locationManager;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (mLocation != null) {
                    latitude = mLocation.getLatitude();
                    longitude = mLocation.getLongitude();
                    Log.i("Debug", "Latitude:" + latitude + "\n Longitude:" + longitude);
                }else {
                    latitude = -12.963004;
                    longitude =  -38.476432;
                }
                LatLng salvador = new LatLng(latitude, longitude);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(salvador).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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

}
