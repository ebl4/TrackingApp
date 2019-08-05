package com.example.mapboxapp.Tracking.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackingRegister {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("codigo")
    @Expose
    private String codigo;
    @SerializedName("idChamado")
    @Expose
    private Integer idChamado;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("erroAplicacao")
    @Expose
    private Boolean erroAplicacao;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getIdChamado() {
        return idChamado;
    }

    public void setIdChamado(Integer idChamado) {
        this.idChamado = idChamado;
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
