package com.example.rafael_cruz.prototipo.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.rafael_cruz.prototipo.R;

public class TransitionActivity extends AppCompatActivity {
    private AlertDialog alerta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
            if (isConected(this)){
            MainActivity.isFinishActivity = true;
            Intent intent = new Intent(TransitionActivity.this,MainActivity.class);
            startActivity(intent);}
            else {
                openDialogLogIn();
            }
    }
    public static boolean isConected(Context cont){
        ConnectivityManager conmag = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conmag != null ) {
            conmag.getActiveNetworkInfo();

            //Verifica internet pela WIFI
            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                return true;
            }

            //Verifica se tem internet móvel
            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                return true;
            }
        }
        return false;
    }

    private void openDialogLogIn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sem conexão com a internet");
        builder.setMessage("Conecte a internet e tente novamente");
        builder.setPositiveButton("Tentar de novo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Log.i("Dialog login: ","Sim");
                Intent intent =  new Intent(TransitionActivity.this,SplashScreen.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Log.i("Dialog login: ","Não");
                finish();
            }
        });
        alerta = builder.create();
        alerta.show();
    }
}
