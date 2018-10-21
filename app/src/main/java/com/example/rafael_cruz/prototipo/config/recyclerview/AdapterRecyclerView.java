/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.prototipo.config.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        ImageView imgIcon;
        TextView txtDescricao;
        TextView txtLocalidade;
        ProgressBar progressBar;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imagemview_list_recycler);
            txtDescricao = itemView.findViewById(R.id.text_descricao_recycler);
            txtLocalidade = itemView.findViewById(R.id.text_localidade_recycler);
            progressBar = itemView.findViewById(R.id.progressBarlistview);
        }
    }

    // Store a member variable for the contacts
    private List<Eventos> mEventosList;
    private Context mContext;
    private String url;
    private ViewHolder mViewHolder;

    // Pass in the contact array into the constructor
    public AdapterRecyclerView(Context context, List<Eventos> eventos) {
        mEventosList = eventos;
        mContext = context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public AdapterRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View listView = inflater.inflate(R.layout.item_list_recyclerview, parent, false);
  //      listView.setOnClickListener(new MainActivity().MyOnClickListener());
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(listView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Eventos eventos = mEventosList.get(position);
        mViewHolder = viewHolder;

        // Set item views based on your views and data model
        TextView mTxtDescricao = mViewHolder.txtDescricao;
        String parse = eventos.getTipoEvento();
        mTxtDescricao.setText(parse);
        TextView mTxtLocalidade = mViewHolder.txtLocalidade;
        parse = eventos.getLocal();
        mTxtLocalidade.setText(parse);
        ImageView imgIcon = mViewHolder.imgIcon;
        url = eventos.getImgDownload();
        if (mViewHolder.imgIcon == null){
            mViewHolder.imgIcon.setVisibility(View.GONE);
            mViewHolder.progressBar.setVisibility(View.VISIBLE);
            StorageReference storageReference =
                    FirebaseStorage.getInstance().getReferenceFromUrl(url);
            mViewHolder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mViewHolder.progressBar.setVisibility(View.GONE);
                            mViewHolder.imgIcon.setVisibility(View.VISIBLE);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mViewHolder.progressBar.setVisibility(View.GONE);
                            mViewHolder.imgIcon.setVisibility(View.VISIBLE);
                            return false;
                        }
                    }).into(mViewHolder.imgIcon);
        } else {
            StorageReference storageReference =
                    FirebaseStorage.getInstance().getReferenceFromUrl(url);
            mViewHolder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(viewHolder.imgIcon);
            mViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mEventosList.size();
    }
}
