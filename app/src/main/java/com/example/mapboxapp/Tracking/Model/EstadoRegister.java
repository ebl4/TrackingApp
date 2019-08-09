package com.example.mapboxapp.Tracking.Model;

import com.example.mapboxapp.Tracking.Model.DadoEstado;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EstadoRegister {

    @SerializedName("dados")
    @Expose
    private List<DadoEstado> dados = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("erroAplicacao")
    @Expose
    private Boolean erroAplicacao;

    public List<DadoEstado> getDados() {
        return dados;
    }

    public void setDados(List<DadoEstado> dados) {
        this.dados = dados;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getErroAplicacao() {
        return erroAplicacao;
    }

    public void setErroAplicacao(Boolean erroAplicacao) {
        this.erroAplicacao = erroAplicacao;
    }

}