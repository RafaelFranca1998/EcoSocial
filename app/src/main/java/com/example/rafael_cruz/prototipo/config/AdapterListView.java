package com.example.rafael_cruz.prototipo.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Classe adaptador Customizada.
 */
public class AdapterListView extends BaseAdapter {
    private List<Eventos> itens;
    private Context context;

    public AdapterListView( Context context, List<Eventos> itens ) {
        //Itens do listview.
        this.itens = itens;
        //Objeto responsável por pegar o Layout do item.
        this.context =  context;
    }

    public Eventos getItem(int position) {
        return itens.get(position);
    }

    @Override
    public int getCount() {
        return itens.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemSuporte itemHolder;
        //se a view estiver nula (nunca criada), inflamos o layout nela (Singleton).
        if (view == null) {
            //infla o layout para podermos pegar as views.
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            try {
                assert mInflater != null;
                view = mInflater.inflate(R.layout.item_list, null);
            } catch (NullPointerException e){
                e.printStackTrace();
            }


            //cria um item de suporte para não precisarmos sempre
            //inflar as mesmas informacoes.
            itemHolder = new ItemSuporte();
            assert view != null;
            itemHolder.txtDescricao = (view.findViewById(R.id.text_descricao_list));
            itemHolder.txtLocalidade = view.findViewById(R.id.text_localidade);
            itemHolder.imgIcon = (view.findViewById(R.id.imagemview_list));

            //define os itens na view.
            view.setTag(itemHolder);
        } else {
            //se a view já existe pega os itens.
            itemHolder = (ItemSuporte) view.getTag();
        }

        //pega os dados da lista
        //e define os valores nos itens.
        Eventos item = itens.get(position);
        //itemHolder.imgIcon.setImageResource(item.getIconeRid());
        itemHolder.txtDescricao.setText(item.getTipoEvento());
        itemHolder.txtLocalidade.setText(item.getLocal());
        //baixa imagem do datastore
        Log.i("URL FOTO ADAPTER", item.getImgDownload());
        String url = item.getImgDownload();
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        //storageReference.toString();
        Glide.with(context).using(new FirebaseImageLoader())
                .load(storageReference)
                .into(itemHolder.imgIcon);

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
