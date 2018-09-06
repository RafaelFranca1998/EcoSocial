package com.example.rafael_cruz.prototipo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rafael_cruz.prototipo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends Fragment{


    public AddEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_event, container, false);

      //  ((MainActivity) getActivity()).setToolbarTitle("Adicionar Eventos");

        return rootView;
    }



}
