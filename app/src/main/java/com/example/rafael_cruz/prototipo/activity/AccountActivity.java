/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.prototipo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.config.AdapterListViewAccount;
import com.example.rafael_cruz.prototipo.config.Base64Custom;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.config.Preferencias;
import com.example.rafael_cruz.prototipo.model.Eventos;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {
    private CircleImageView imgAccount;
    private Button btChangeImg;
    private TextView emailUser;
    private TextView nameUser;

    private String url;
    private String idUser;
    private String linkDownload;
    private Uri pathLocalImg;
    private double progress;
    private ProgressDialog pd;


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
        emailUser = findViewById(R.id.txt_account_emailuser);
        nameUser = findViewById(R.id.txt_account_username);
        //------------------------------------------------------------------------------------------
        Preferencias preferencias = new Preferencias(AccountActivity.this);
        idUser = Base64Custom.codificarBase64(preferencias.getEmail());
        url = "gs://ecossocial-2c0dc.appspot.com/images/account/"+ idUser +"/image_account.png";
        databaseReference = DAO.getFireBase().child("usuarios").child(idUser).child("user_events");
        //------------------------------------------------------------------------------------------
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Conta");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emailUser.setText(preferencias.getEmail());
        nameUser.setText(preferencias.getNome()+" "+preferencias.getSobrenome());

        storageReference = DAO.getFirebaseStorage()
                .child("images")
                .child("account")
                .child(idUser)
                .child("image_account.png");
        linkDownload = storageReference.getPath();
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
        //todo update
        //update2(linkDownload);
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
        final long ONE_MEGABYTE = 1024 * 1024;
        pd = new ProgressDialog(AccountActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Carregando");
        pd.show();
        // mProgressBar.setProgress((int)progress);
        System.out.println("Upload is " + progress + "% done");
        reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgAccount.setImageBitmap(bitmap);
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Log.e("Erro: ",exception.getMessage());
            }
        });
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
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                try {
                    Bitmap imagem = MediaStore.Images.Media.getBitmap((AccountActivity.this).getContentResolver(), pathLocalImg);
                    // comprimir no formato jpeg
                    ByteArrayOutputStream stream =  new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,60,stream);
                    byte[] byteData = stream.toByteArray();
                    UploadTask uploadTask = storageReference.putBytes(byteData);
                    final ProgressDialog pd2 = new ProgressDialog(AccountActivity.this);
                    // Listen for state changes, errors, and completion of the upload.
                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            pd2.setCancelable(false);
                            pd2.setProgress((int)progress);
                            pd2.setMessage("Carregando ("+ (int)progress+"%)");
                            pd2.show();
                            System.out.println("Upload is " + progress + "% done");
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused");
                            pd2.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(AccountActivity.this,"Falha ao carregar a imagem",Toast.LENGTH_SHORT).show();
                            pd2.dismiss();
                            updateImgAccount();
                            exception.printStackTrace();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd2.dismiss();
                            Toast.makeText(AccountActivity.this,"Imagem carregada!",Toast.LENGTH_SHORT).show();
                            updateImgAccount();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                updateImgAccount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public  void writeImg(Bitmap bmp){
        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Ecosocial/Profile";
            File dir = new File(file_path);
            if(!dir.exists())
                dir.mkdirs();
            File file = new File(dir, "user_image.png");
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void baixarImagem(Uri imagemUri){
        StorageReference islandRef = DAO.getFirebaseStorage().child(imagemUri.toString());
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                writeImg(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Erro: ",exception.getMessage());
            }
        });
    }
    public void deleteImg(){
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Ecosocial/Profile";
        File f0 = new File(dir, "user_image.png");
        boolean d0 = f0.delete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgAccount.setImageBitmap(null);
    }
}
