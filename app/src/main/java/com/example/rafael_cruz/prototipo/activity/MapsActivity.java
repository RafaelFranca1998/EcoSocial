package com.example.rafael_cruz.prototipo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    int REQUEST_LOCATION;
    private static GoogleMap mGoogleMap;
    List<Marker> markerList;
    List<Eventos> eventosList;
    LocationManager locationManager;
    Location mLocation;
    Double latitude, longitude;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //----------------------------------------TOOLBAR-------------------------------------------
        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(getString(R.string.eventos));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //------------------------------------------------------------------------------------------
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

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
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
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
                        Intent intent =  new Intent(MapsActivity.this, InfoEventoActivity.class);
                        intent.putExtra("eventos", eventos.getEventId());
                        startActivity(intent);
                    }
                }
                return false;
            }
        });

        locationManager = MainActivity.locationManager;
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this,
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
        Bundle extra = getIntent().getExtras();
        if (extra != null){
            latitude = extra.getDouble("lat");
            longitude = extra.getDouble("lon");
        }

        LatLng salvador = new LatLng(latitude, longitude);
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(salvador).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
}
