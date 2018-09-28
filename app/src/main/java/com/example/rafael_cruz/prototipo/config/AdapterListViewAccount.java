package com.example.rafael_cruz.prototipo.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Classe adaptador Customizada.
 */
public class AdapterListViewAccount extends BaseAdapter {
    private List<Eventos> itens;
    private Context context;
    private StorageReference storageReference;
    private Eventos item;

    public AdapterListViewAccount(Context context, List<Eventos> itens ) {
        //Itens do listview.
        this.itens = itens;
        //Objeto responsável por pegar o Layout do item.
        this.context =  context;
    }



    @Override
    public int getCount() {
        return itens.size();
    }

    @Override
    public Object getItem(int position) {
        return itens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemSuporte itemHolder;
        View view = convertView;
        //se a view estiver nula (nunca criada), inflamos o layout nela (Singleton).
        if (view == null) {
            //infla o layout para podermos pegar as views.
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            view = mInflater.inflate(R.layout.item_list_account, null);

            //cria um item de suporte para não precisarmos sempre
            //inflar as mesmas informacoes.
            itemHolder = new ItemSuporte();
            itemHolder.txtDescricao = (view.findViewById(R.id.teste_descricao));
            itemHolder.txtLocalidade = view.findViewById(R.id.text_localidade_account);
            itemHolder.imgIcon = (view.findViewById(R.id.imagemview_list_account));
            itemHolder.chkRemover = view.findViewById(R.id.chekbox_remover);
            //define os itens na view.
            view.setTag(itemHolder);
        } else {
            //se a view já existe pega os itens.
            itemHolder = (ItemSuporte) view.getTag();
        }

        //pega os dados da lista
        //e define os valores nos itens.
        item = itens.get(position);
        //itemHolder.imgIcon.setImageResource(item.getIconeRid());
        itemHolder.txtDescricao.setText(item.getTipoEvento());
        itemHolder.txtLocalidade.setText(item.getLocal());
        //baixa imagem do datastore
        Log.i("URL FOTO ADAPTER", item.getImgDownload());
        String url = item.getImgDownload();
        storageReference =
                FirebaseStorage.getInstance().getReferenceFromUrl(url);
        //storageReference.toString();
        Glide.with(context).using(new FirebaseImageLoader())
                .load(storageReference)
                .into(itemHolder.imgIcon);

        itemHolder.chkRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDialogExcluir(position);
            }
        });
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
        CheckBox chkRemover;
    }

    private void abrirDialogExcluir(int position) {
        final int i = position;
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //define o titulo
        builder.setTitle("Excluir?");
        //define a mensagem
        builder.setMessage("Realmente quer apagar este evento?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(context, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                excluirEvento(i);
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(context, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //cria o AlertDialog
        AlertDialog alerta = builder.create();
        //Exibe
        alerta.show();
    }

    private void excluirEvento(int position){
        Eventos eventosExcluir = itens.get(position);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(eventosExcluir.getImgDownload());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        String key = eventosExcluir.getEventId();
        String emailUser = eventosExcluir.getAutorEmail();
        DatabaseReference databaseReference = DAO.getFireBase().child("events").child(key);
        databaseReference.removeValue();

        DatabaseReference databaseReference2 = DAO.getFireBase().child("usuarios")
                .child(emailUser)
                .child("user_events")
                .child(key);
        databaseReference2.removeValue();// remove o evento dentro da conta do usuário
        // cria a referencia com base : events/push/evento.class
        return;
    }
}
