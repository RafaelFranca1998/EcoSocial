//package com.example.rafael_cruz.prototipo.fragments;
//
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.app.TimePickerDialog;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.text.format.DateFormat;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.RadioGroup;
//import android.widget.Switch;
//import android.widget.TimePicker;
//import android.widget.Toast;
//
//import com.example.rafael_cruz.prototipo.R;
//import com.example.rafael_cruz.prototipo.activity.MainActivity;
//import com.example.rafael_cruz.prototipo.config.Base64Custom;
//import com.example.rafael_cruz.prototipo.config.DAO;
//import com.example.rafael_cruz.prototipo.config.Preferencias;
//import com.example.rafael_cruz.prototipo.config.data_e_hora.Data;
//import com.example.rafael_cruz.prototipo.config.data_e_hora.Hora;
//import com.example.rafael_cruz.prototipo.model.Eventos;
//import com.github.rtoshiro.util.format.SimpleMaskFormatter;
//import com.github.rtoshiro.util.format.text.MaskTextWatcher;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
//import com.google.android.gms.maps.MapsInitializer;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class AddEventFragment extends DialogFragment implements OnMapReadyCallback
//        , LocationListener {
//    //Atributos do mapa
//    int REQUEST_LOCATION;
//    MapView mMapView;
//    private static GoogleMap mGoogleMap;
//    Location mLocation;
//    Double latitude,longitude, lat, lng;
//    private static LatLng position;
//    LocationManager locationManager;
//    LocationListener locationListener;
//    boolean isDate;
//    private String provider;
//    private Eventos eventos;
//    private EditText outroEditText;
//    private Button buttonAvancar;
//    private Button btCompartilhar;
//    private RadioGroup radioGroup;
//    private String tipoevento;
//    private Switch aSwitchSemlimitetempo;
//    private static EditText editTextData;
//    private static EditText editTextHora;
//    private static int year_x, month_x, day_x, hour_x, minute_x, hora;
//    private static String local;
//    DatabaseReference database;
//    StorageReference storageReference;
//
//    List<Address> addresses;
//    Geocoder geocoder;
//
//
//    View rootView;
//    Activity activity;
//
//    public AddEventFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // Inflate the layout for this fragment
//        rootView = inflater.inflate(R.layout.fragment_add_event, container, false);
//        //((MainActivity) getActivity()).setToolbarTitle("Adicionar Eventos");
//
//        //diz a atividade principal que estamos em um fragment
//        MainActivity.isInFragment = true;
//
//        activity = getActivity();
//
//        geocoder = new Geocoder(getActivity(), Locale.getDefault());
//
//        btCompartilhar = rootView.findViewById(R.id.bt_compartilhar);
//        radioGroup = rootView.findViewById(R.id.radioGroup);
//        outroEditText = rootView.findViewById(R.id.editText_outro);
//        buttonAvancar = rootView.findViewById(R.id.button_adicionar_evento);
//        aSwitchSemlimitetempo = rootView.findViewById(R.id.switch_sem_limite_tempo);
//        editTextData = rootView.findViewById(R.id.edit_text_data_2);
//        editTextHora = rootView.findViewById(R.id.edit_text_hora);
//
//        //mascara para a data hora
//        SimpleMaskFormatter simpleMaskdata = new SimpleMaskFormatter("NN/NN/NNNN");
//        SimpleMaskFormatter simpleMaskhora = new SimpleMaskFormatter("NN:NN");
//        MaskTextWatcher maskData = new MaskTextWatcher(editTextData, simpleMaskdata);
//        MaskTextWatcher maskHora = new MaskTextWatcher(editTextHora, simpleMaskhora);
//        editTextData.addTextChangedListener(maskData);
//        editTextHora.addTextChangedListener(maskHora);
//
//        //database
////        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
////        reference.child("01").setValue("40");
//
//        final Calendar calendar = Calendar.getInstance();
//        year_x = calendar.get(Calendar.YEAR);
//        month_x = calendar.get(Calendar.MONTH);
//        day_x = calendar.get(Calendar.DAY_OF_MONTH);
//
//        //TODO Listeners de views
//        editTextData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                DialogFragment newFragment = new SelectDateFragment();
//                newFragment.show(getFragmentManager(), "DatePicker");
//                // getActivity().onCreateDialog(DIALOG_ID_DATE);
//                isDate = true;
//            }
//        });
//
//        editTextHora.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isDate = false;
//                DialogFragment newFragment = new TimePickerFragment();
//                newFragment.show(getFragmentManager(), "TimePicker");
//                //     getActivity().showDialog(DIALOG_ID_TIME);
//            }
//        });
//
//        btCompartilhar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                compartilharFoto();
//            }
//        });
//
//        buttonAvancar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addEvento();
//            }
//        });
//
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.radio_button_animal_perdido) {
//                    tipoevento = "Animal Perdido";
//                    outroEditText.setEnabled(false);
//                    Toast.makeText(activity, "Animal", Toast.LENGTH_LONG).show();
//                } else if (checkedId == R.id.radio_button_coleta_de_lixo) {
//                    tipoevento = "Coleta de lixo";
//                    outroEditText.setEnabled(false);
//                    Toast.makeText(activity, "Coleta", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//
//        aSwitchSemlimitetempo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (aSwitchSemlimitetempo.isActivated()) {
//                    Log.i("if", String.valueOf(aSwitchSemlimitetempo.isActivated()));
//                    editTextHora.setEnabled(false);
//                    hora = 0000;
//                    editTextData.setText(hora);
//                } else {
//                    Log.i("else", String.valueOf(aSwitchSemlimitetempo.isActivated()));
//                    editTextHora.setEnabled(true);
//                    editTextHora.setText("0000");
//                    hora = Integer.parseInt(editTextHora.getText().toString().replace(":", ""));
//                }
//            }
//        });
//
//
//        // todo inicializa o mapa
//        mMapView = rootView.findViewById(R.id.mapView2);
//        mMapView.onCreate(savedInstanceState);
//        mMapView.onResume(); // needed to get the map to display immediately
//        provider = MainActivity.provider;
//        if (provider == null) {
//            provider = "gps";
//        }
//
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // TODO Verificação de permissões
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    REQUEST_LOCATION);
//        } else {
//        }
//
//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
////        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
////                .addConnectionCallbacks(this)
////                .addOnConnectionFailedListener(this)
////                .addApi(LocationServices.API).build();
////
////        mGoogleApiClient.connect();
//
//
//        mMapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap mMap) {
//                mGoogleMap = mMap;
//
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions( //Method of Fragment
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                REQUEST_LOCATION
//                        );
//                    }
//                } else {
//                    mGoogleMap.setMyLocationEnabled(true);
//                }
//                //todo por enquanto está null
////                locationManager = (LocationManager)getActivity().getSystemService(provider);
//                locationManager = MainActivity.locationManager;
////                locationListener.onLocationChanged(mLocation);
//                mLocation = locationManager.getLastKnownLocation(provider);
//                if (mLocation != null) {
//                    latitude = mLocation.getLatitude();
//                    longitude = mLocation.getLongitude();
//                    Log.i("Debug", "Latitude:" + latitude + "\n Longitude:" + longitude);
//                }
//
//                // For showing a move to my location button
//
//
//                // For dropping a marker at a point on the Map
//                //todo verificação lat/lng
//                LatLng salvador;
//                try {
//                    salvador = new LatLng(latitude, longitude);
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                    salvador = new LatLng(-13.003257, -38.523767);
//                } catch (Exception e) {
//                    salvador = new LatLng(-13.003257, -38.523767);
//                    e.printStackTrace();
//                }
//                mGoogleMap.addMarker(new MarkerOptions().position(salvador).title("Coleta de Lixo")
//                        .snippet("Local: Sua casa \n Horario: o dia todo ").draggable(true));
//
//                // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(salvador).zoom(12).build();
//                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//
//                mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//                    @Override
//                    public void onMarkerDragStart(Marker marker) {
//                        // Here your code
//                        Toast.makeText(getActivity(), "Dragging Start",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onMarkerDrag(Marker marker) {
//                        // Toast.makeText(MainActivity.this, "Dragging",
//                        // Toast.LENGTH_SHORT).show();
//                        System.out.println("Draagging");
//                    }
//
//                    @Override
//                    public void onMarkerDragEnd(Marker marker) {
//                        position = marker.getPosition(); //
//                        Toast.makeText(
//                                getActivity(),
//                                "Lat " + position.latitude + " "
//                                        + "Long " + position.longitude,
//                                Toast.LENGTH_LONG).show();
//                        lat = position.latitude;
//                        lng = position.longitude;
//                    }
//                });
//            }
//        });
//
//
//        return rootView;
//    }
//
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        return new Dialog(getActivity(), getTheme()) {
//            @Override
//            public void onBackPressed() {
//                // On backpress, do your stuff here.
//                MainFragment nextFrag = new MainFragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
//                        .addToBackStack(null)
//                        .commit();
//            }
//        };
//    }
//
//    public void addEvento() {
//        eventos = new Eventos();
//        eventos.setData((Data.dateToString(year_x, month_x, day_x)));
//        eventos.setHorario(Hora.hourToString(hour_x, minute_x));
//        if (tipoevento.equals("Outro...")) {
//            eventos.setTipoEvento(outroEditText.getText().toString());
//        } else {
//            eventos.setTipoEvento(tipoevento);
//        }
//        if (lat == null && lng == null) {
//            lat = latitude;
//            lng = longitude;
//        }
//        eventos.setLat(lat);
//        eventos.setLon(lng);
//
//        if (local != null) {
//            eventos.setLocal(local);
//        }
//
//        try {
//            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
//        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
//        eventos.setLocal(address);
//        Preferencias preferencias = new Preferencias(getActivity());
//        eventos.setAutorEmail(Base64Custom.codificarBase64(preferencias.getEmail()));
//        eventos.setIdUsuario(preferencias.getId());
//        database = DAO.getFireBase();
//        database.child("events")
//                .push()
//                .setValue(eventos);// cria a referencia com base : events/push/evento.class
//        database.child("usuarios")
//                .child(Base64Custom.codificarBase64(preferencias.getEmail()))
//                .child("user_events")
//                .push()
//                .setValue(eventos);// insere o evento dentro da conta do usuário
//        Log.i("Debug: ",database.getRef().toString());
//
//
//        Toast.makeText(getActivity(),"Adicionado!",Toast.LENGTH_SHORT).show();
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.fragment_container, new MainFragment(), "NewFragmentTag");
//        ft.commit();
//    }
//    public void compartilharFoto(){
//        Intent intent =  new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        getActivity().startActivityForResult(intent,1);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode,resultCode,data);
//        // testa processo de retorno
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
//
//            storageReference = DAO.getFirebaseStorage()
//                    .child("images")
//                    .child("events")
//                    .child(eventos.getEventId())
//                    .child("image_eevent.png");
//            // recuperar local do arquivo
//            Uri localImagemSelecionada = data.getData();
//            // recupera a imagem do local que foi selecionado
//            try {
//                Bitmap imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),localImagemSelecionada);
//                // comprimir no formato png
//                ByteArrayOutputStream stream =  new ByteArrayOutputStream();
//                imagem.compress(Bitmap.CompressFormat.PNG,75,stream);
//                byte[] byteData = stream.toByteArray();
//                UploadTask uploadTask = storageReference.putBytes(byteData);
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        e.printStackTrace();
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Uri uri = taskSnapshot.getUploadSessionUri();
//                    }
//                });
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mGoogleMap = googleMap;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mMapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mMapView.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mMapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mMapView.onLowMemory();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mLocation = location;
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
//
//    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar calendar = Calendar.getInstance();
//            int yy = calendar.get(Calendar.YEAR);
//            int mm = calendar.get(Calendar.MONTH);
//            int dd = calendar.get(Calendar.DAY_OF_MONTH);
//            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
//        }
//
//        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
//            year_x = yy;
//            month_x = mm + 1;
//            day_x = dd;
//            Log.i("dia: ",String.valueOf(day_x));
//            Log.i("mes: ",String.valueOf(month_x));
//            Log.i("ano: ",String.valueOf(year_x));
//            editTextData.setText(Data.dateToString(year_x,month_x,day_x));
//        }
//    }
//
//    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
//        public TimePickerFragment() {
//            // Required empty public constructor
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current time as the default values for the picker
//
//            final Calendar calendar = Calendar.getInstance();
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);
//
//            // Create a new instance of TimePickerDialog and return it
//            return new TimePickerDialog(getActivity(), this, hour, minute,
//                    DateFormat.is24HourFormat(getActivity()));
//        }
//
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            // Do something with the time chosen by the user
//            hour_x = hourOfDay;
//            minute_x = minute;
//            editTextHora.setText(Hora.hourToString(hour_x,minute_x));
//        }
//    }
//
//}
//
