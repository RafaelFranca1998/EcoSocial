/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.prototipo.config;

import android.util.Base64;

public class Base64Custom {

    public static String codificarBase64(String texto){
        String code = Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");;
        code = code.replace("=","");
        return code;
    }

    public static String decodificarBase64(String textoCodificado){
       return new String( Base64.decode(textoCodificado, Base64.DEFAULT));
    }

    public static String removeUrl(String textoUrl){
        return textoUrl.replaceAll("https://ecossocial-2c0dc.firebaseio.com/events/","");
    }
}
