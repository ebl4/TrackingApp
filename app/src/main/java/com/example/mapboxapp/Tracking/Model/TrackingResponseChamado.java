package com.example.mapboxapp.Tracking.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackingResponseChamado {

    @SerializedName("operacaoEntrada1")
    @Expose
    private Boolean operacaoEntrada1;
    @SerializedName("operacaoEntrada2")
    @Expose
    private Boolean operacaoEntrada2;
    @SerializedName("operacaoEntrada3")
    @Expose
    private Boolean operacaoEntrada3;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("erroAplicacao")
    @Expose
    private Boolean erroAplicacao;

    public Boolean getOperacaoEntrada1() {
        return operacaoEntrada1;
    }

    public Boolean getOperacaoEntrada2() {
        return operacaoEntrada2;
    }


    public Boolean getOperacaoEntrada3() {
        return operacaoEntrada3;
    }


    public String getMessage() {
        return message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getErroAplicacao() {
        return erroAplicacao;
    }

}
