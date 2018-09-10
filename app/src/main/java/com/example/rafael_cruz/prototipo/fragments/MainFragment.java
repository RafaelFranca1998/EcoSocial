package com.example.rafael_cruz.prototipo.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.rafael_cruz.prototipo.config.AdapterListView;
import com.example.rafael_cruz.prototipo.config.ItemListView;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.model.Eventos;

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
    private ItemListView        itemListView;
    private List<ItemListView>  itens;


    public MainFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listView = rootView.findViewById(R.id.list);



       // String[] dados = new String[] { "Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread",
       //         "Honeycomb", "Ice Cream Sandwich", "Jelly Bean",
       //         "KitKat", "Lollipop", "Marshmallow", "Nougat" };

       // ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dados);

      //  listView.setAdapter(adapter);

        itemListView =  new ItemListView();

        itens = new ArrayList<ItemListView>();

        addEvent("Cachorro perdido","Federação",R.drawable.icon_cachorro_perdido);

        addEvent("Coleta de Lixo","Ribeira",R.drawable.icons8_rss_50);

        AdapterListView adapterListView =  new AdapterListView(context,itens);

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

    /**
     * Metodo para adicionar eventos.
     * @param descricao
     * @param localidade
     * @param imagem
     */
    public void addEvent(String descricao, String localidade, int imagem){
        itemListView =  new ItemListView();

        itemListView.setTextoDescricao(FINAL_DESCRICAO+descricao);

        itemListView.setTextoLocalidade(FINAL_LOCALIDADE+localidade);

        itemListView.setIconeRid(imagem);

        itens.add(itemListView);
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
}


