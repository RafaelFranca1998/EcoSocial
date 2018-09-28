package com.example.rafael_cruz.prototipo.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.example.rafael_cruz.prototipo.model.Usuario;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private Toolbar toolbar;
    ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_evento);
        //------------------------------------------------------------------------------------------
        txtNome = findViewById(R.id.txt_info_nome);
        txtEmail= findViewById(R.id.txt_info_email);
        txtEndereco= findViewById(R.id.txt_info_endereco);
        txtDescricao= findViewById(R.id.txt_info_descricao);
        txtTelefone= findViewById(R.id.txt_info_telefone);
        imgFotoEvento = findViewById(R.id.image_info);
        progressBar = findViewById(R.id.progressBar_info);
        //------------------------------------------------------------------------------------------
        Bundle extra = getIntent().getExtras();

        if (extra!= null){ // checa e obtem chave do evento selecionado
            keyEvent = extra.getString("eventos");
            databaseReference = DAO.getFireBase().child("events");
        }
        //---------------------------------CONFIGURA TOOLBAR----------------------------------------
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Informações");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
        //------------------------------------------------------------------------------------------
        eventos = new Eventos();

        usuario =  new Usuario();

        getDatabase1();
    }
    /**
     * Metodo para baixar a imagem referente ao evento.
     */
    private void getImg(){
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        storageReference.toString();
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
     * Obtem os dados do evento do banco de dados.
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
     * Obtem os dados do usuario.
     */
    private void getDatabase2(){
        databaseReference2 = DAO.getFireBase().child("usuarios");
        databaseReference2.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    String key3 = data.getKey().toString();
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
