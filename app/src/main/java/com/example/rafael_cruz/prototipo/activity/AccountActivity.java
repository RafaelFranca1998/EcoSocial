package com.example.rafael_cruz.prototipo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.config.AdapterListViewAccount;
import com.example.rafael_cruz.prototipo.config.Base64Custom;
import com.example.rafael_cruz.prototipo.config.CircleTransform;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.config.Preferencias;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends AppCompatActivity {
    private ImageView imgAccount;
    private Button btChangeImg;
    private TextView emailUser;
    private TextView nameUser;

    private String url;
    private String idUser;
    private String linkDownload;
    private Uri pathLocalImg;
    private double progress;
    private ProgressBar progressBar;


    private DatabaseReference databaseReference;
    public StorageReference storageReference;
    private ValueEventListener valueEventListener;

    private List<Eventos> listEventos;
    private ListView listView;
    private AdapterListViewAccount adapterListView;

    @Override
    protected void onStart() {
        super.onStart();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Eventos eventos = listEventos.get(position);
                Intent intent =  new Intent(AccountActivity.this, InfoEventoActivity.class);
                intent.putExtra("eventos", eventos.getEventId());
                startActivity(intent);
                Toast.makeText(AccountActivity.this,"nada ainda",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //------------------------------------------------------------------------------------------
        listView = findViewById(R.id.account_lv);
        imgAccount = findViewById(R.id.img_account);
        btChangeImg = findViewById(R.id.bt_trocar_ft);
        progressBar = findViewById(R.id.progressBar2);
        emailUser = findViewById(R.id.txt_account_emailuser);
        nameUser = findViewById(R.id.txt_account_username);
        //------------------------------------------------------------------------------------------
        Preferencias preferencias = new Preferencias(AccountActivity.this);
        idUser = Base64Custom.codificarBase64(preferencias.getEmail());
        url = "gs://ecossocial-2c0dc.appspot.com/images/account/"+ idUser +"/image_account.png";
        databaseReference = DAO.getFireBase().child("usuarios").child(idUser).child("user_events");
        //------------------------------------------------------------------------------------------
        emailUser.setText(preferencias.getEmail());
        nameUser.setText(preferencias.getNome()+" "+preferencias.getSobrenome());

        storageReference = DAO.getFirebaseStorage()
                .child("images")
                .child("account")
                .child(idUser)
                .child("image_account.png");

        listEventos =  new ArrayList<>();
        getListEvent();
        adapterListView =  new AdapterListViewAccount(AccountActivity.this,listEventos);
        listView.setAdapter(adapterListView);

        btChangeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImg();
            }
        });
        updateImgAccount();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            pathLocalImg = data.getData();
            sendImg();
        }
    }

    /**
     * Method to obtain the path for images.
     */
    public void shareImg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent =  new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        }).start();
    }

    /**
     * Method to update the user image.
     */
    private void updateImgAccount(){
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        if (!this.isFinishing ()) {
            progressBar.setVisibility(View.VISIBLE);
            imgAccount.setVisibility(View.GONE);


            Glide.with(AccountActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(reference)
                    .transform(new CircleTransform(this))
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                            imgAccount.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            imgAccount.setImageResource(R.drawable.ic_account_box);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            imgAccount.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(imgAccount);
        }
    }

    private void getListEvent(){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listEventos.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    Eventos eventos = data.getValue(Eventos.class);
                    eventos.setEventId((data.getKey()));
                    eventos.setTipoEvento(data.getValue(Eventos.class).getTipoEvento());
                    eventos.setLocal(data.getValue(Eventos.class).getLocal());
                    eventos.setImgDownload(data.getValue(Eventos.class).getImgDownload());

                    listEventos.add(eventos);
                }
                adapterListView.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }


    private void sendImg(){
        storageReference = DAO.getFirebaseStorage()
                .child("images")
                .child("account")
                .child(idUser)
                .child("image_account.png");
        try {
            Bitmap imagem = MediaStore.Images.Media.getBitmap((AccountActivity.this).getContentResolver(), pathLocalImg);
            // comprimir no formato jpeg
            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.JPEG,60,stream);
            byte[] byteData = stream.toByteArray();
            UploadTask uploadTask = storageReference.putBytes(byteData);

            // Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //                   showProgressBar(true);
                    //YourAsyncTask asyncTask = new YourAsyncTask();
                    //asyncTask.onPreExecute();
//                        pd = new ProgressDialog(AddEventActivity.this);
//                        pd.setMessage("Carregando");
//                        pd.show();
//                    mProgressBar.setProgress((int)progress);
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
                    // pd.dismiss();
                    updateImgAccount();
                    Toast.makeText(AccountActivity.this,"Falha ao carregar a imagem",Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // pd.dismiss();
                    updateImgAccount();
                    Toast.makeText(AccountActivity.this,"Imagem carregada!",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateImgAccount();
    }
}
