package com.example.rafael_cruz.prototipo.model;

import android.util.Log;

import java.util.Calendar;

public class Eventos {

    private int data,horario;
    static int dataCriacao;
    private String tipoEvento,local,descricao;
    private double lat,lon;


    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getHorario() {
        return horario;
    }

    public void setHorario(int horario) {
        this.horario = horario;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public static int getDataCriacao() {
        return dataCriacao;
    }



    public void setDataCriacao(int dataCriacao) {
        this.dataCriacao = dataCriacao;
    }


    public Eventos() {
        //Obtem data atual
        final Calendar calendar = Calendar.getInstance();
        String parse = calendar.get(Calendar.DAY_OF_MONTH)+""+calendar.get(Calendar.MONTH)+""+calendar.get(Calendar.YEAR);
        this.dataCriacao = Integer.parseInt(parse);
        Log.i("data criacao",parse);
    }

    public Eventos(int data, int horario, String tipoEvento, String local, String descricao, double lat, double lon) {
        this.data = data;
        this.horario = horario;
        this.tipoEvento = tipoEvento;
        this.local = local;
        this.descricao = descricao;
        this.lat = lat;
        this.lon = lon;
    }
}
