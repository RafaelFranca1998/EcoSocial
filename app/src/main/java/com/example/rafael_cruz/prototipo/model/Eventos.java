package com.example.rafael_cruz.prototipo.model;

import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

public class Eventos {

    private String data,horario;
    static int dataCriacao,iconeRid;
    private String tipoEvento,local,descricao,idUsuario,eventId, autorEmail;
    private double lat,lon;
    private String imgDownload;


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

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setDataCriacao(int dataCriacao) {
        this.dataCriacao = dataCriacao;
    }


    public Eventos() {
       this("","","","","","","",-13.003257,-38.523767,-1);
        data = "";
        horario= "";
        tipoEvento = "";
        local = "";
        descricao = "";
        idUsuario = "";
        eventId = "";
        lat = -13.003257;
        lon = -38.523767;
        iconeRid = 0;
    }

    public Eventos(String data, String horario, String tipoEvento, String local, String descricao, String idUsuario, String eventId, double lat, double lon,int iconeRid) {
        this.data = data;
        this.horario = horario;
        this.tipoEvento = tipoEvento;
        this.local = local;
        this.descricao = descricao;
        this.idUsuario = idUsuario;
        this.eventId = eventId;
        this.lat = lat;
        this.lon = lon;
        this.iconeRid = iconeRid;
    }

    public String getImgDownload() {
        return imgDownload;
    }

    public void setImgDownload(String imgDownload) {
        this.imgDownload = imgDownload;
    }

    public String getAutorEmail() {
        return autorEmail;
    }

    public void setAutorEmail(String autorEmail) {
        this.autorEmail = autorEmail;
    }

    public int getIconeRid() {
        return iconeRid;
    }

    public void setIconeRid(int iconeRid) {
        Eventos.iconeRid = iconeRid;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
