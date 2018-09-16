package com.example.rafael_cruz.prototipo.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.rafael_cruz.prototipo.activity.MainActivity;
import com.example.rafael_cruz.prototipo.config.AdapterListView;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.config.ItemEvento;
import com.example.rafael_cruz.prototipo.config.ItemListView;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private ListView            listView;
    private Context             context;
    private Toolbar             toolbar;
    private String              FINAL_DESCRICAO      = "Descrição: ";
    private String              FINAL_LOCALIDADE     = "Localidade: ";
    private String              FINAL_TAG_EVENTOS    = "events";
    private List<ItemEvento>       listEventos;
    private AdapterListView adapterListView;
    DatabaseReference           databaseReference;
    String TAG_DEBUG = "Debug: ";


    public MainFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listView = rootView.findViewById(R.id.list);

        MainActivity.isFinsihActivity = true;
        MainActivity.isInFragment = false;
        listEventos =  new ArrayList<>();
        databaseReference = DAO.getFireBase().child(FINAL_TAG_EVENTOS);

        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listEventos.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    ItemEvento eventos = new ItemEvento();
                    eventos.setTipoEvento(data.getValue(Eventos.class).getTipoEvento());
                    eventos.setLocal(data.getValue(Eventos.class).getLocal());
                    eventos.setIconeRid(R.drawable.cachorro_map_icon);
                    listEventos.add(eventos);
                }
                adapterListView.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);

//        for (int i = 0; listEventos.size()< i ; i++){
//            Eventos eventos_1 = listEventos.get(i);
//            Log.i(TAG_DEBUG+"eventos_1:"+i,listEventos.get(i).toString());
//            eventos_1 = new Eventos();
//            eventos_1.setLocal(listEventos.get(i).getLocal());
//            eventos_1.setDescricao(listEventos.get(i).getDescricao());
//            String tipoEvento = listEventos.get(i).getTipoEvento();
//            if (tipoEvento.equals("Animal Perdido")){
//                eventos_1.setIconeRid(R.drawable.icon_cachorro_perdido);
//            }else {
//                eventos_1.setIconeRid(R.drawable.icons8_rss_50);
//            }
//            Log.i(TAG_DEBUG+"itemListView:"+i,eventos_1.toString());
//
//        }
        adapterListView =  new AdapterListView(context,listEventos);

        listView.setAdapter(adapterListView);
      //  ((MainActivity) getActivity()).setToolbarTitle("Inicio");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context,"nada ainda",Toast.LENGTH_LONG).show();
            }
        });

        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
}


