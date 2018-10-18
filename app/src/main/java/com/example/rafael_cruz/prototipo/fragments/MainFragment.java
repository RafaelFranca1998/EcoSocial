package com.example.rafael_cruz.prototipo.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rafael_cruz.prototipo.activity.InfoEventoActivity;
import com.example.rafael_cruz.prototipo.activity.MainActivity;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.config.recyclerview.AdapterRecyclerView;
import com.example.rafael_cruz.prototipo.config.recyclerview.RecyclerItemClickListener;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private Context context;
    private String FINAL_TAG_EVENTOS = "events";

    private List<Eventos> listEventos;
    private RecyclerView recyclerView;
    private AdapterRecyclerView adapterListView;

    private ValueEventListener valueEventListener;

    private DatabaseReference databaseReference;
    public StorageReference storageReference;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        getListEvent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview_main);

        MainActivity.isFinishActivity = true;
        MainActivity.isInFragment = false;
        databaseReference = DAO.getFireBase().child(FINAL_TAG_EVENTOS);
        //--------------------------CONFIGURA ADAPTER-----------------------------------------------
        listEventos =  new ArrayList<>();
        adapterListView =  new AdapterRecyclerView(context,listEventos);
        recyclerView.setAdapter(adapterListView);
        LinearLayoutManager gridLayoutManager =
                new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Eventos eventos = listEventos.get(position);
                        Intent intent =  new Intent(getActivity(), InfoEventoActivity.class);
                        intent.putExtra("eventos", eventos.getEventId());
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    private void getListEvent(){
        storageReference = DAO.getFirebaseStorage();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listEventos.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    Eventos eventos = new Eventos();
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

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener( valueEventListener );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener( valueEventListener );
    }
}


