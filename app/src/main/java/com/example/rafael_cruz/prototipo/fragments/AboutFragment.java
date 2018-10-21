/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.prototipo.fragments;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.activity.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {



    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =inflater.inflate(R.layout.fragment_about, container, false);

        MainActivity.isFinishActivity = false;
        MainActivity.isInFragment = true;

        return  root;
    }
}
