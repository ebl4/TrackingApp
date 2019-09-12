package com.example.mapboxapp.Tracking.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FirstTrackingRegister {
    @SerializedName("idChamado")
    @Expose
    private Integer idChamado;
    @SerializedName("nomeEntrada1")
    @Expose
    private String nomeEntrada1;
    @SerializedName("valorEntrada1")
    @Expose
    private String valorEntrada1;
    @SerializedName("valorAuxiliarEntrada1")
    @Expose
    private String valorAuxiliarEntrada1;
    @SerializedName("nomeEntrada2")
    @Expose
    private String nomeEntrada2;
    @SerializedName("valorEntrada2")
    @Expose
    private String valorEntrada2;
    @SerializedName("valorAuxiliarEntrada2")
    @Expose
    private String valorAuxiliarEntrada2;
    @SerializedName("nomeEntrada3")
    @Expose
    private String nomeEntrada3;
    @SerializedName("valorEntrada3")
    @Expose
    private String valorEntrada3;
    @SerializedName("valorAuxiliarEntrada3")
    @Expose
    private String valorAuxiliarEntrada3;

    public void setIdChamado(Integer idChamado) {
        this.idChamado = idChamado;
    }

    public void setNomeEntrada1(String nomeEntrada1) {
        this.nomeEntrada1 = nomeEntrada1;
    }

    public void setValorEntrada1(String valorEntrada1) {
        this.valorEntrada1 = valorEntrada1;
    }

    public void setValorAuxiliarEntrada1(String valorAuxiliarEntrada1) {
        this.valorAuxiliarEntrada1 = valorAuxiliarEntrada1;
    }

    public void setNomeEntrada2(String nomeEntrada2) {
        this.nomeEntrada2 = nomeEntrada2;
    }

    public void setValorEntrada2(String valorEntrada2) {
        this.valorEntrada2 = valorEntrada2;
    }

    public void setValorAuxiliarEntrada2(String valorAuxiliarEntrada2) {
        this.valorAuxiliarEntrada2 = valorAuxiliarEntrada2;
    }

    public void setNomeEntrada3(String nomeEntrada3) {
        this.nomeEntrada3 = nomeEntrada3;
    }

    public void setValorEntrada3(String valorEntrada3) {
        this.valorEntrada3 = valorEntrada3;
    }

    public void seValorAuxiliarEntrada3(String valorAuxiliarEntrada3) {
        this.valorAuxiliarEntrada3 = valorAuxiliarEntrada3;
    }

    public Integer getIdChamado() {
        return idChamado;
    }

    public String getNomeEntrada1() {
        return nomeEntrada1;
    }

    public String getValorEntrada1() {
        return valorEntrada1;
    }

    public String getValorAuxiliarEntrada1() {
        return valorAuxiliarEntrada1;
    }

    public String getNomeEntrada2() {
        return nomeEntrada2;
    }

    public String getValorEntrada2() {
        return valorEntrada2;
    }

    public String getValorAuxiliarEntrada2() {
        return valorAuxiliarEntrada2;
    }

    public String getNomeEntrada3() {
        return nomeEntrada3;
    }

    public String getValorEntrada3() {
        return valorEntrada3;
    }

    public String geValorAuxiliarEntrada3() {
        return valorAuxiliarEntrada3;
    }
}
