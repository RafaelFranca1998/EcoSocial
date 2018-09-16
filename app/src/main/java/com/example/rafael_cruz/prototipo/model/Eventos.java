package com.example.rafael_cruz.prototipo.model;

import android.util.Log;

import java.util.Calendar;

public class Eventos {

    private String data,horario;
    static int dataCriacao,iconeRid;
    private String tipoEvento,local,descricao;
    private double lat,lon;


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
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
    }

    public Eventos(String data, String horario, String tipoEvento, String local, String descricao, double lat, double lon) {
        this.data = data;
        this.horario = horario;
        this.tipoEvento = tipoEvento;
        this.local = local;
        this.descricao = descricao;
        this.lat = lat;
        this.lon = lon;
    }

    public static int getIconeRid() {
        return iconeRid;
    }

    public static void setIconeRid(int iconeRid) {
        Eventos.iconeRid = iconeRid;
    }
}
