package com.example.rafael_cruz.prototipo.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.config.Preferencias;
import com.example.rafael_cruz.prototipo.fragments.AboutFragment;
import com.example.rafael_cruz.prototipo.fragments.MainFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    private Toolbar toolbar;
    private CircleImageView imageLogo;
    private Bitmap bitmap;
    private FirebaseAuth auntenticacao;
    private NavigationView navigationView;
    //atributo da classe.
    private AlertDialog alerta;
    private StorageReference islandRef;
    private Uri uri;

    public static LocationManager locationManager;
    public static String provider;
    //caso estiver na atividade principal
    public static boolean isFinishActivity = false;
    //caso estiver em um fragment
    public static boolean isInFragment = false;

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-------------------------------------TOOLBAR-----------------------
        toolbar = findViewById(R.id.toolbar);
        setToolbarTitle("Inicio");
        setSupportActionBar(toolbar);
        //-----------------------------NAVIGATION VIEW------------------------
        navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_principal);
        //---------------------------------------------------------------------
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }
        // Obtém a referência da view de cabeçalho
        View headerView = navigationView.getHeaderView(0);

        // Obtém a referência do nome do usuário e altera seu nome
        TextView txtLogin = headerView.findViewById(R.id.usuario_nome_login);
        TextView txtEmail = headerView.findViewById(R.id.textView_nav_header_email);
        TextView txtNome = headerView.findViewById(R.id.textview_nav_header_nome);
        imageLogo = headerView.findViewById(R.id.imageViewLogo);

        if (verificarUsuarioLogado()) {
            Preferencias preferencias = new Preferencias(MainActivity.this);
            txtLogin.setText(R.string.exit);
            txtEmail.setText(preferencias.getEmail());
            txtNome.setText(preferencias.getNome()+" "+preferencias.getSobrenome());
            try {
                uri = Uri.parse(preferencias.getLinkImg());
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            baixarImagem(uri);
            txtLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auntenticacao =  DAO.getFirebaseAutenticacao();
                    auntenticacao.signOut();
                    Intent intent = new Intent(MainActivity.this,TransitionActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            txtLogin.setText(R.string.fazer_login);
            txtLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(MainActivity.this,LoginActivity.class);
                    startActivity( intent );
                }
            });
        }

        //-----------------------------INICIA FRAGMENT------------------------
        MainFragment fragment = new MainFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isFinishActivity && !isInFragment){
            openDialogExit();
        } else if (isInFragment){
            super.onBackPressed();
        }
    }

    /**
     * open a dialog to confirm the exit.
     */
    private void openDialogExit() {
        //create builder for AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle("Sair?");
        //set message
        builder.setMessage("Realmente quer sair?");
        //set positive button
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //   Toast.makeText(MainActivity.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        });
        //set negative button
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // Toast.makeText(MainActivity.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //create AlertDialog
        alerta = builder.create();
        //show
        alerta.show();
    }

    private void openDialogLogIn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Para Adicionar um caso é preciso estar logado");
        builder.setMessage("Fazer login?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Log.i("Dialog login: ","Sim");
                Intent intent =  new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Log.i("Dialog login: ","Não");
            }
        });
        alerta = builder.create();
        alerta.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about){
            AboutFragment fragment = new AboutFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            setToolbarTitle("Sobre o Ecosocial");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_principal) {
            MainFragment fragment = new MainFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_eventos) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                    startActivity(intent);
                }
            }).start();

        } else if (id == R.id.nav_marcar_evento) {
            if (verificarUsuarioLogado()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                        startActivity(intent);
                    }
                }).start();
            } else {
                openDialogLogIn();
            }
        } else if (id == R.id.nav_account){
            if (verificarUsuarioLogado()){
                Intent intent = new Intent(MainActivity.this,AccountActivity.class);
                startActivity(intent);
            } else {
                Intent intent =  new Intent(this,LoginActivity.class);
                startActivity(intent);
            }

        }else if (id == R.id.nav_share) {
            String text = "Vamos fazer uma boa ação? Baixe o EcoSocial! Http://www.ecosocial.com/download/";
            Uri pictureUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/user_photo.png");
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Compartilhar App"));
        }

        DrawerLayout drawer;
        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * modify the title from toolbar.
     * @param title
     *
     */
    public void setToolbarTitle(String title){
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    private boolean verificarUsuarioLogado(){
        auntenticacao =  DAO.getFirebaseAutenticacao();
        return auntenticacao.getCurrentUser() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // todo request updates from locations
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        baixarImagem(uri);
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        navigationView.setCheckedItem(R.id.nav_principal);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    public void baixarImagem(Uri imagemUri){
        try {
            islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(imagemUri.toString());
            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageLogo.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("Erro: ",exception.getMessage());
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
            imageLogo.setImageResource(R.drawable.logo_prototipo_mateus);
        }

    }

    public  void writeImg(Bitmap bmp){
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] bytes = stream.toByteArray();
            //this creates a png file in internal folder
            //the directory is like : ......data/sketches/my_sketch_437657436.png
//            File mFileTemp = new File(getFilesDir() + File.separator
//                    + "sketches"
//                    , "my_sketch_"
//                    + System.currentTimeMillis() + ".png");
//            mFileTemp.getParentFile().mkdirs();


            String fileName = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/user_photo"+System.currentTimeMillis()+".png");
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(bytes);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
