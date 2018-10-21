/*
 * Copyright (c) 2018. all rights are reserved to the authors of this project,
 * unauthorized use of this code in other projects may result in legal complications.
 */

package com.example.rafael_cruz.prototipo.config;

import java.util.Date;
/**
ainda não vamos usar
*/
public class ItemEvento {
    private String   tipoEvento,Local;
    private int iconeRid;

    public ItemEvento(String tipoEvento, String local, int iconeRid) {
        this.tipoEvento = tipoEvento;
        Local = local;
        this.iconeRid = iconeRid;
    }

    public ItemEvento() {
        this("","", -1);
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getLocal() {
        return Local;
    }

    public void setLocal(String local) {
        Local = local;
    }

    public int getIconeRid() {
        return iconeRid;
    }

    public void setIconeRid(int iconeRid) {
        this.iconeRid = iconeRid;
    }
}
