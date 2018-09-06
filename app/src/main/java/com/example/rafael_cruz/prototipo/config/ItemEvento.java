package com.example.rafael_cruz.prototipo.config;

import java.util.Date;
@Deprecated
/**
ainda não vamos usar
*/
public class ItemEvento {
    private int   tipoEvento; // 1 =  Animal perdido 2 = Coleta de lixo 3 = Outro(requer especificação *tipoOutro*)
    private String tipoOutro; // todo limitar a 40 caracteres
    private Date     horario; // data* só vai ser usado caso diatodo seja true
    private boolean  diaTodo;

    public int getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(int tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getTipoOutro() {
        return tipoOutro;
    }

    public void setTipoOutro(String tipoOutro) {
        this.tipoOutro = tipoOutro;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public boolean isDiaTodo() {
        return diaTodo;
    }

    public void setDiaTodo(boolean diaTodo) {
        this.diaTodo = diaTodo;
    }
}
