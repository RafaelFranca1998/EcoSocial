package com.example.rafael_cruz.prototipo.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.activity.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    int REQUEST_LOCATION;
    MapView mMapView;
    private static GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        MainActivity.isInFragment = true;

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

                // For showing a move to my location button
                populateMap();
                // For dropping a marker at a point on the Map
                LatLng salvador = new LatLng(-12.999998, -38.493746);
                googleMap.addMarker(new MarkerOptions().position(salvador).title("Coleta de Lixo")
                                                            .snippet("Local: Sua casa \n Horario: o dia todo ").draggable(true));

                LatLng ribeira =  new LatLng(-12.921654, -38.511641);
                googleMap.addMarker(new MarkerOptions().position(ribeira).title("Coleta de Lixo")
                                                        .snippet("Local: Rua Arthur Matos \n Horario: 15:20"));

                LatLng cachoroPerdido =  new LatLng(-12.999212, -38.499147);
                googleMap.addMarker(new MarkerOptions().position(cachoroPerdido).title("Cachorro Perdido")
                                                            .snippet("Local: Rua Sergio de Carvalho \n Horario: Dia todo"));


                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(ribeira).zoom(12).build();
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

    private void populateMap(){
        final LatLng MELBOURNE = new LatLng(-37.813, 144.962);
        Marker melbourne = googleMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_cachorro_perdido)));
    }

}
