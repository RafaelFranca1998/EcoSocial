package com.example.rafael_cruz.prototipo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.rafael_cruz.prototipo.R;

public class SplashScreen extends AppCompatActivity {
    // Timer da splash screen
    private static int SPLASH_TIME_OUT = 10000;
    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.GET_ACCOUNTS,
            android.Manifest.permission.READ_PHONE_NUMBERS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(SplashScreen.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            checkAllPermissions();
        }
        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                // Esse método será executado sempre que o timer acabar
                // E inicia a activity principal
                Intent i = new Intent(SplashScreen.this, TransitionActivity.class);
                startActivity(i);
                // Fecha esta activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void checkAllPermissions() {
        for (String permission : PERMISSIONS) {
            int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
            if (ActivityCompat.checkSelfPermission(SplashScreen.this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashScreen.this,PERMISSIONS ,
                        PERMISSION_GRANTED);
                SPLASH_TIME_OUT =1000;

            }else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this,
                       permission)) {
                    ActivityCompat.requestPermissions(SplashScreen.this,
                            PERMISSIONS, PERMISSION_GRANTED);
                    SPLASH_TIME_OUT =1000;
                }
            }
        }

    }

}
