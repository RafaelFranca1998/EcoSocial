package com.example.rafael_cruz.prototipo.fragments;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.rafael_cruz.prototipo.R;
import com.google.android.gms.fido.fido2.api.common.RequestOptions;

public class AccountFragment extends Fragment {
    private ImageView imgAccount;
    private Uri url;

    public AccountFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        imgAccount = root.findViewById(R.id.img_account);

        Glide.with(getActivity()).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgAccount) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create((getActivity()).getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imgAccount.setImageDrawable(circularBitmapDrawable);
            }
        });
        return root;
    }

}
