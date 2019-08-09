package com.example.mapboxapp.Tracking.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IdEstado {
    @SerializedName("idEstado")
    @Expose
    private String idEstado;

    public IdEstado(String idEstado){
        this.idEstado = idEstado;
    }

    public String getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }
}