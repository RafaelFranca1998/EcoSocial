package com.example.rafael_cruz.prototipo.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.activity.MainActivity;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
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

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends Fragment implements OnMapReadyCallback {

    //Atributos do mapa
    int REQUEST_LOCATION;
    MapView mMapView;
    private static GoogleMap mGoogleMap;



    static Eventos eventos;

    private EditText outroEditText;
    private Button buttonAvancar;

    private RadioGroup radioGroup;
    private LatLng localizacao;
    private String      tipoevento;
    private Switch aSwitchSemlimitetempo;
    private EditText    editTextData;
    private EditText    editTextHora;
    private int         year_x,month_x,day_x,hour_x,minute_x,hora;
    static final int    DIALOG_ID_DATE = 0;
    static final int    DIALOG_ID_TIME = 1;
    Activity activity;

    public AddEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_event, container, false);

        ((MainActivity) getActivity()).setToolbarTitle("Adicionar Eventos");

        activity = getActivity();

        radioGroup              = rootView.findViewById(R.id.radioGroup);
        outroEditText           = rootView.findViewById(R.id.editText_outro);
        buttonAvancar           = rootView.findViewById(R.id.button_adicionar_evento);
        aSwitchSemlimitetempo   = rootView.findViewById(R.id.switch_sem_limite_tempo);
        editTextData            = rootView.findViewById(R.id.editText_data_2);
        editTextHora            = rootView.findViewById(R.id.editText_hora);

        //mascara para a data hora
        SimpleMaskFormatter simpleMaskdata = new SimpleMaskFormatter( "NN/NN/NNNN" );
        SimpleMaskFormatter simpleMaskhora = new SimpleMaskFormatter( "NN:NN" );
        MaskTextWatcher maskData = new MaskTextWatcher(editTextData,simpleMaskdata);
        MaskTextWatcher maskHora = new MaskTextWatcher(editTextHora,simpleMaskhora);
        editTextData.addTextChangedListener(maskData);
        editTextHora.addTextChangedListener(maskHora);

        //database
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        reference.child("01").setValue("40");

        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);


        editTextData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showDialog(DIALOG_ID_DATE);
            }
        });

        editTextHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showDialog(DIALOG_ID_TIME);
            }
        });


        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // addEvent();
//                SelectOnMapFragment fragment = new SelectOnMapFragment();
//                android.support.v4.app.FragmentTransaction fragmentTransaction =
//                        getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container_add_event, fragment);
//                fragmentTransaction.commit();
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("message");
//                myRef.setValue("Hello, World!");
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_animal_perdido){
                    tipoevento = "Animal Perdido";
                    outroEditText.setEnabled(false);
                    Toast.makeText(activity,"Animal",Toast.LENGTH_LONG).show();
                } else if (checkedId == R.id.radio_button_coleta_de_lixo){
                    tipoevento = "Coleta de lixo";
                    outroEditText.setEnabled(false);
                    Toast.makeText(activity,"Coleta",Toast.LENGTH_LONG).show();
                } else if (checkedId == R.id.radio_button_outro){
                    outroEditText.setEnabled(true);
                    tipoevento = outroEditText.getText().toString();
                }
            }
        });




        aSwitchSemlimitetempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aSwitchSemlimitetempo.isActivated()){
                    Log.i("if",String.valueOf( aSwitchSemlimitetempo.isActivated()));
                    editTextHora.setEnabled(false);
                    hora = 0000;
                    editTextData.setText(hora);
                }else {
                    Log.i("else",String.valueOf( aSwitchSemlimitetempo.isActivated()));
                    editTextHora.setEnabled(true);
                    editTextHora.setText("0000");
                    hora = Integer.parseInt(editTextHora.getText().toString().replace(":",""));
                }
            }
        });


        mMapView = rootView.findViewById(R.id.mapView2);
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
                mGoogleMap = mMap;

                // For showing a move to my location button
                populateMap();
                // For dropping a marker at a point on the Map
                LatLng salvador = new LatLng(-12.999998, -38.493746);
                mGoogleMap.addMarker(new MarkerOptions().position(salvador).title("Coleta de Lixo")
                        .snippet("Local: Sua casa \n Horario: o dia todo ").draggable(true));

                LatLng ribeira =  new LatLng(-12.921654, -38.511641);
                mGoogleMap.addMarker(new MarkerOptions().position(ribeira).title("Coleta de Lixo")
                        .snippet("Local: Rua Arthur Matos \n Horario: 15:20"));

                LatLng cachoroPerdido =  new LatLng(-12.999212, -38.499147);
                mGoogleMap.addMarker(new MarkerOptions().position(cachoroPerdido).title("Cachorro Perdido")
                        .snippet("Local: Rua Sergio de Carvalho \n Horario: Dia todo"));


                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(ribeira).zoom(12).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // For showing a move to my location button

        // For dropping a marker at a point on the Map
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions( //Method of Fragment
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION
                );
            }
        } else {

        }


        mGoogleMap.setMyLocationEnabled(true);
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void populateMap(){
        final LatLng MELBOURNE = new LatLng(-37.813, 144.962);
        Marker melbourne = mGoogleMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_prototipo_mateus)));
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

}
