package com.example.rafael_cruz.prototipo.activity;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.config.Base64Custom;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.config.Preferencias;
import com.example.rafael_cruz.prototipo.config.RandomKey;
import com.example.rafael_cruz.prototipo.config.data_e_hora.Data;
import com.example.rafael_cruz.prototipo.config.data_e_hora.Hora;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity implements
        OnMapReadyCallback, LocationListener {

    //------------------------------------------MAP-------------------------------------------------
    private int             REQUEST_LOCATION;
    private static MapView  mMapView;
    private static GoogleMap mGoogleMap;
    private static Location mLocation;
    private static Double   latitude,longitude, lat, lng;
    private static LatLng   position;
    private LocationManager locationManager;
    private String          provider;
    private static LatLng   salvador;
    private static LatLng   latLng;
    private List<Address>   addresses;
    private Geocoder        geocoder;
    private static Marker   marker;
    //-------------------------------------------VIEWS----------------------------------------------
    private Eventos     eventos;
    private Button      buttonAvancar;
    private Button      btCompartilhar;
    private TextView    tvCaminho;
    private TextView    tvData;
    private RadioGroup  radioGroup;
    private String      tipoevento;
    private String      idUsuario;
    String country;
    String locality;
    private boolean         isClicked;
    private static EditText editTextData;
    private static EditText editTextHora;
    private static EditText editTextNome;
    private EditText        editTextDescricao;
    private static int      year_x, month_x, day_x, hour_x, minute_x;
    private static String   local;
    private String          linkDownload;
    private DatabaseReference database;
    private StorageReference storageReference;
    //------------------------------------------DIALOG----------------------------------------------
    private double progress;
    private ProgressDialog pd;
    //------------------------------------------CALENDAR--------------------------------------------
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private TimePickerDialog.OnTimeSetListener time;
    //----------------------------------------------------------------------------------------------
    private static Uri      pathSelectedImg;
    public FragmentManager  fragmentManager;
    private ProgressBar     mProgressBar;
    private LinearLayout    llHora;
    private LinearLayout    llNome;
    private LinearLayout    llDataEHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        //------------------------------------------------------------------------------------------
        Toolbar toolbar = findViewById(R.id.toolbar2);
        mProgressBar = findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        radioGroup = findViewById(R.id.radioGroup);
        isClicked = false;
        fragmentManager = getSupportFragmentManager();

        geocoder = new Geocoder(AddEventActivity.this, Locale.getDefault());

        Preferencias preferencias =  new Preferencias(this);
        idUsuario = preferencias.getId();
        //-----------------------------------LAYOUTS------------------------------------------------
        llHora = findViewById(R.id.ll_hora);
        llNome = findViewById(R.id.ll_nome);
        llDataEHora = findViewById(R.id.ll_hora_e_data);
        //-----------------------------------TEXTVIEWS----------------------------------------------
        tvCaminho = findViewById(R.id.txt_caminho_imagem);
        tvData = findViewById(R.id.tv_data);
        //-----------------------------------BOTÕES-------------------------------------------------
        buttonAvancar = findViewById(R.id.button_adicionar_evento);
        btCompartilhar = findViewById(R.id.bt_upload);
        //-----------------------------------EDITTEXT-----------------------------------------------
        editTextData = findViewById(R.id.edit_text_data_2);
        editTextHora = findViewById(R.id.edit_text_hora);
        editTextNome = findViewById(R.id.editText_nome);
        editTextDescricao = findViewById(R.id.txt_detalhes);
        //------------------------------------------------------------------------------------------
        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);
        //------------------------------------------------------------------------------------------

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelData();
            }
        };

        time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateLabelHora();
            }
        };


        //TODO Listeners de views
        editTextData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddEventActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editTextHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddEventActivity.this,time,myCalendar
                        .get(Calendar.HOUR_OF_DAY),myCalendar
                        .get(Calendar.MINUTE),true).show();
            }
        });

        btCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilharFoto();
            }
        });

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarFoto();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_animal_perdido) {
                    tipoevento = "Animal Perdido";
                    editTextHora.setEnabled(false);
                    llNome.setVisibility(View.VISIBLE);
                    llHora.setVisibility(View.GONE);
                    tvData.setText("Data do desaparecimento: ");
                    llDataEHora.setOrientation(LinearLayout.VERTICAL);
                    isClicked = true;
                    //Animal
                } else if (checkedId == R.id.radio_button_coleta_de_lixo) {
                    tipoevento = "Coleta de lixo: ";
                    llNome.setVisibility(View.GONE);
                    llHora.setVisibility(View.VISIBLE);
                    llDataEHora.setOrientation(LinearLayout.HORIZONTAL);
                    tvData.setText("Data: ");
                    isClicked = true;
                    editTextHora.setEnabled(true);
                    //Coleta de lixo
                }
            }
        });

        // todo inicializa o mapa
        mMapView = findViewById(R.id.mapView2);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        provider = MainActivity.provider;
        if (provider == null) {
            provider = "gps";
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        }
        try {
            MapsInitializer.initialize(AddEventActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------------------------------------
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;

                if (ActivityCompat.checkSelfPermission(AddEventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions( //Method of Fragment
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION
                        );
                    }
                } else {
                    mGoogleMap.setMyLocationEnabled(true);
                }
                locationManager = MainActivity.locationManager;
                mLocation = locationManager.getLastKnownLocation(provider);
                if (mLocation != null) {
                    latitude = mLocation.getLatitude();
                    longitude = mLocation.getLongitude();
                    Log.i("Debug", "Latitude:" + latitude + "\n Longitude:" + longitude);
                }

                // For showing a move to my location button
                // For dropping a marker at a point on the Map
                //todo verificação lat/lng
                try {
                    salvador = new LatLng(latitude, longitude);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    salvador = new LatLng(-13.003257, -38.523767);
                } catch (Exception e) {
                    salvador = new LatLng(-13.003257, -38.523767);
                    e.printStackTrace();
                }
                if (marker == null) {
                    marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(salvador)
                            .title("Clique e arraste")
                            .draggable(true));
                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(salvador).zoom(12)
                            .build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    mGoogleMap.addMarker(new MarkerOptions().position(marker.getPosition())
                            .title(marker.getTitle())
                            .draggable(true));
                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(marker.getPosition())
                            .zoom(12).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }

                mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        geocoder = new Geocoder(AddEventActivity.this);
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            if (addressList != null && addressList.size() > 0) {
                                String locality = addressList.get(0).getAddressLine(0);
                                String country = addressList.get(0).getCountryName();
                                // marker.setPosition(latLng);
                                position = marker.getPosition();
                                lat = position.latitude;
                                lng = position.longitude;
                                if (!locality.isEmpty() && !country.isEmpty()) {
                                    salvador = new LatLng(latLng.latitude, latLng.longitude);
                                    Log.i("Local_Debug: " ,locality + "  " + country);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


                mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        latLng = mGoogleMap.getCameraPosition().target;
                        marker.setPosition(latLng);
                    }
                });
            }
        });
        //------------------------------------------------------------------------------------------

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
    }

    public void updateLabelData() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
        editTextData.setText(sdf.format(myCalendar.getTime()));
    }

    public void updateLabelHora() {
        String myFormat = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
//        hour_x = myCalendar.getTime().getHours();
//        minute_x = myCalendar.getTime().getMinutes();
        editTextHora.setText(sdf.format(myCalendar.getTime()));

    }

    public void showProgressBar(boolean exibir){
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // testa processo de retorno
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            pathSelectedImg = data.getData();
            assert pathSelectedImg != null;
            tvCaminho.setText(pathSelectedImg.getPath());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
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
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    /**
     *Method to add events.
     */
    public void addEvento() {
        try {
            eventos = new Eventos();
            String keyEvent = RandomKey.randomAlphaNumeric(20);
            eventos.setData((Data.dateToString(year_x, month_x, day_x)));
            eventos.setHorario(Hora.hourToString(hour_x, minute_x));
            eventos.setTipoEvento(tipoevento);
            eventos.setEventId(keyEvent);
            eventos.setDescricao(editTextDescricao.getText().toString());
            if (lat == null && lng == null) {
                lat = latitude;
                lng = longitude;
            }
            eventos.setLat(lat);
            eventos.setLon(lng);
            Log.i("Debug: ", linkDownload);
            eventos.setImgDownload(linkDownload);

            if (local != null) {
                eventos.setLocal(local);
            }

            try {
                addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            eventos.setLocal(address);
            Preferencias preferencias = new Preferencias(AddEventActivity.this);
            eventos.setNome(editTextNome.getText().toString());
            eventos.setAutorEmail(Base64Custom.codificarBase64(preferencias.getEmail()));
            eventos.setIdUsuario(preferencias.getId());
            database = DAO.getFireBase();
            database.child("events")
                    .child(keyEvent)
                    .setValue(eventos);// cria a referencia com base : events/push/evento.class
            database.child("usuarios")
                    .child(Base64Custom.codificarBase64(preferencias.getEmail()))
                    .child("user_events")
                    .child(keyEvent)
                    .setValue(eventos);// insere o evento dentro da conta do usuário
            Log.i("Debug: ", database.getRef().toString());
            marker = null;
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(this,"Preencha todos os Campos."+ e.getMessage(),Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"Erro desconhecido "+e.toString(),Toast.LENGTH_LONG);
        }finally {
            Toast.makeText(AddEventActivity.this,"Adicionado!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddEventActivity.this,MainActivity.class);
            startActivity(intent);
        }

    }

    public void compartilharFoto(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent =  new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        }).start();
    }

    private void enviarFoto(){
        storageReference = DAO.getFirebaseStorage()
                .child("images")
                .child("events")
                .child(idUsuario)
                .child(RandomKey.randomAlphaNumeric(10))// Sempre usar 10 caracteres
                .child("image_event.png");
        linkDownload = storageReference.toString();
        try {
            if (!isClicked)throw new Exception();
            if (editTextData.getText().equals("")) throw new Exception();
            Bitmap imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), pathSelectedImg);
            // comprimir no formato png
            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.JPEG,40,stream);
            byte[] byteData = stream.toByteArray();
            UploadTask uploadTask = storageReference.putBytes(byteData);

            // Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    showProgressBar(true);
                    pd = new ProgressDialog(AddEventActivity.this);
                    pd.setCancelable(false);
                    pd.setMessage("Carregando");
                    pd.show();
                    mProgressBar.setProgress((int)progress);
                    System.out.println("Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                    pd.dismiss();
                    Toast.makeText(AddEventActivity.this,"Falha ao carregar a imagem",Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    addEvento();
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    pd.dismiss();
                }
            });

        } catch (IOException e) {
            Toast.makeText(this,"Erro ao enviar a mensagem! "+ e.getMessage() ,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (NullPointerException e){
            Toast.makeText(this,"É necessário selecionar uma foto! "+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"Erro desconhecido! "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
