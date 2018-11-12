/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.prototipo.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.fragments.MapsFragment;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.example.rafael_cruz.prototipo.model.Usuario;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InfoEventoActivity extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------
    private String keyEvent;
    private String keyUsuario;
    private String url;
    //----------------------------------------------------------------------------------------------
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private StorageReference storageReference;
    private Eventos eventos;
    private Usuario usuario;
    //----------------------------------------------------------------------------------------------
    private TextView txtNome;
    private TextView txtEmail;
    private TextView txtEndereco;
    private TextView txtDescricao;
    private TextView txtTelefone;
    //----------------------------------------------------------------------------------------------
    private ImageView imgFotoEvento;
    //----------------------------------------------------------------------------------------------
    private Button btSeeOnMap;
    private Button btShare;
    //----------------------------------------------------------------------------------------------
    private Toolbar toolbar;
    ProgressBar progressBar;
    public static boolean isMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_evento);
        //------------------------------------------------------------------------------------------
        txtNome         = findViewById(R.id.txt_info_nome);
        txtEmail        = findViewById(R.id.txt_info_email);
        txtEndereco     = findViewById(R.id.txt_info_endereco);
        txtDescricao    = findViewById(R.id.txt_info_descricao);
        txtTelefone     = findViewById(R.id.txt_info_telefone);
        btSeeOnMap      = findViewById(R.id.see_on_map);
        btShare         = findViewById(R.id.button_share);
        imgFotoEvento   = findViewById(R.id.image_info);
        progressBar     = findViewById(R.id.progressBar_info);
        //------------------------------------------------------------------------------------------
        Bundle extra = getIntent().getExtras();

        if (extra!= null){ // checa e obtem chave do evento selecionado
            keyEvent = extra.getString("eventos");
            databaseReference = DAO.getFireBase().child("events");
        }
        //---------------------------------CONFIGURA TOOLBAR----------------------------------------
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getColor(R.color.Branco));
        }
        toolbar.setTitle(getString(R.string.info));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //------------------------------------------------------------------------------------------
        eventos = new Eventos();

        usuario =  new Usuario();

        getDatabase1();
        //------------------------------------------------------------------------------------------
        btSeeOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoEventoActivity.this,MapsActivity.class);
                intent.putExtra("lat",eventos.getLat());
                intent.putExtra("lon",eventos.getLon());
                startActivity(intent);
            }
        });

        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareEvent();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isMap){
            isMap = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MapsFragment fragment= new MapsFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, fragment);
                        fragmentTransaction.commit();
                    }catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        finish();
                    }
                }
            }).start();
        }else{
            super.onBackPressed();
        }

    }

    private void shareEvent(){

        String tipo = eventos.getTipoEvento();
        String text = "";
        if (tipo.equals(R.string.animal_perdido)){
            text = "Atenção! Animal perdido em "+eventos.getLocal()+"\n você viu o "+eventos.getNome()+" ?\n ajude-nos.";
        } else {
            text = "Atenção! coleta de lixo no dia "

                    +eventos.getData()+"as "+eventos.getHorario()+"\n Endereço "+eventos.getLocal()+"\n ajude-nos a melhorar o mundo.";
        }
        imgFotoEvento.buildDrawingCache();
        Bitmap icon = imgFotoEvento.getDrawingCache();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri pictureUri = Uri.parse("file:///sdcard/temporary_file.jpg");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Compartilhar App"));
    }

    private void shareLocation(){
        String uri = "geo:" + eventos.getLat() + ","
                +eventos.getLon() + "?q=" + eventos.getLat()
                + "," + eventos.getLon();
        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }

    /**
     * Method to download the image related to the event.
     */
    private void getImg(){
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        if (!this.isFinishing ()) {
            if (imgFotoEvento == null){
                imgFotoEvento.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
            Glide.with(this).using(new FirebaseImageLoader())
                    .load(storageReference)
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    imgFotoEvento.setVisibility(View.VISIBLE);
                    return false; // important to return false so the error placeholder can be placed
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    imgFotoEvento.setVisibility(View.VISIBLE);
                    return false;
                }}).into(imgFotoEvento);
        }
    }

    /**
     * Gets the event data from the database.
     */
    private void getDatabase1(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    Log.i("Debug:",dataSnapshot.getChildren().toString());
                    Log.i("Debug:",keyEvent);
                    String key2 = data.getKey();
                    assert key2 != null;
                    if ( key2.equals(keyEvent) ) {
                        eventos = data.getValue(Eventos.class);
                        assert eventos != null;
                        txtDescricao.setText(eventos.getDescricao());
                        txtEndereco.setText(eventos.getLocal());
                        keyUsuario = eventos.getIdUsuario();
                        url = eventos.getImgDownload();
                        getDatabase2();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Gets the user's data.
     */
    private void getDatabase2(){
        databaseReference2 = DAO.getFireBase().child("usuarios");
        databaseReference2.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    String key3 = data.getKey();
                    if (key3.equals(keyUsuario)){
                        usuario = data.getValue(Usuario.class);
                        txtNome.setText(usuario.getNome()+" "+usuario.getSobreNome());
                        txtEmail.setText(usuario.getEmail());
                        txtTelefone.setText(usuario.getTelefone());
                    }
                }
                getImg();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
