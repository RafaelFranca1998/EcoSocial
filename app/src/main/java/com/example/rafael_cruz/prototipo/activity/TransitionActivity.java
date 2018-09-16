package com.example.rafael_cruz.prototipo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.rafael_cruz.prototipo.R;

public class TransitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
            MainActivity.isFinsihActivity = true;
            Intent intent = new Intent(TransitionActivity.this,MainActivity.class);
            startActivity(intent);

    }
}
