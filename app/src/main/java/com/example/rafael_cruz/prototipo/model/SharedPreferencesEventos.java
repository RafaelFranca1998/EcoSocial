/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.prototipo.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.util.HashMap;

public class SharedPreferencesEventos {

    private Context                     contexto;
    private SharedPreferences           preferences;
    private String                      NOME_ARQUIVO    = "evento.eventoPreferencias";
    private int                         MODE            = 0;
    private SharedPreferences.Editor    editor;
    //Chaves finais
    private String                      CHAVE_NOME      = "tipoEvento";
    private String                      CHAVE_DIA_TODO  = "diatodo";
    private String                      CHAVE_DATA      = "data";
    private String                      CHAVE_HORA      = "hora";
    private String                      CHAVE_LAT      = "lat";
    private String                      CHAVE_LON      = "lon";
    private String                      CHAVE_DESCRICAO = "descricao";
    private String                      CHAVE_LOCAL = "local";


    public SharedPreferencesEventos(Context contextoParametro) {
        contexto    = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO,MODE);
        editor      = preferences.edit();
    }

    public void salvarUsuarioPreferences(String nome, boolean diaTodo, String data,String hora, Location location,String descricao,String local){

        editor.putString(CHAVE_NOME,nome);
        editor.putBoolean(CHAVE_DIA_TODO,diaTodo);
        editor.putString(CHAVE_DATA,data);
        editor.putString(CHAVE_HORA,hora);
        editor.putString(CHAVE_LAT,String.valueOf(location.getLatitude()));
        editor.putString(CHAVE_LON,String.valueOf(location.getLongitude()));
        editor.putString(CHAVE_DESCRICAO,descricao);
        editor.putString(CHAVE_LOCAL,local);
        editor.commit();

    }


    public HashMap<String,String> getDadosUsuario(){
        HashMap<String,String> dadosUsuario =  new HashMap<>();
        dadosUsuario.put        (CHAVE_NOME,preferences.getString(CHAVE_NOME,null));
        dadosUsuario.put        (CHAVE_DIA_TODO,preferences.getString(CHAVE_DIA_TODO,null));
        dadosUsuario.put        (CHAVE_DATA,preferences.getString(CHAVE_DATA,null));
        dadosUsuario.put        (CHAVE_HORA,preferences.getString(CHAVE_DATA,null));
        dadosUsuario.put        (CHAVE_LAT,preferences.getString(CHAVE_LAT,null));
        dadosUsuario.put        (CHAVE_LON,preferences.getString(CHAVE_LON,null));
        return dadosUsuario;
    }


}
