package com.example.rafael_cruz.prototipo.config;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.model.Eventos;

import java.util.List;

/**
 * Classe adaptador Customizada
 */
public class AdapterListView extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ItemEvento> itens;
    private Context context;


    public AdapterListView( Context context, List<ItemEvento> itens ) {
        //Itens do listview
        this.itens = itens;
        //Objeto responsável por pegar o Layout do item.
        mInflater = LayoutInflater.from(context);
        this.context =  context;
    }
    @Override
    public int getCount() {
        return itens.size();
    }
    public ItemEvento getItem(int position) {
        return itens.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemSuporte itemHolder;
        //se a view estiver nula (nunca criada), inflamos o layout nela.
        if (view == null) {
            //infla o layout para podermos pegar as views
            //view = mInflater.inflate(R.layout.item_list, null);
            LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.item_list, null);


            //cria um item de suporte para não precisarmos sempre
            //inflar as mesmas informacoes
            itemHolder = new ItemSuporte();
            itemHolder.txtDescricao = ((TextView) view.findViewById(R.id.text_descricao_list));
            itemHolder.txtLocalidade = view.findViewById(R.id.text_localidade);
            itemHolder.imgIcon = ((ImageView) view.findViewById(R.id.imagemview));

            //define os itens na view;
            view.setTag(itemHolder);
        } else {
            //se a view já existe pega os itens.
            itemHolder = (ItemSuporte) view.getTag();
        }

        //pega os dados da lista
        //e define os valores nos itens.
        ItemEvento item = itens.get(position);
        itemHolder.txtDescricao.setText(item.getTipoEvento());
        itemHolder.txtLocalidade.setText(item.getLocal());
        itemHolder.imgIcon.setImageResource(item.getIconeRid());

        //retorna a view com as informações
        return view;
    }

    /**
     * Classe de suporte para os itens do layout.
     */
    private class ItemSuporte {
        ImageView imgIcon;
        TextView txtLocalidade;
        TextView txtDescricao;
    }
}
