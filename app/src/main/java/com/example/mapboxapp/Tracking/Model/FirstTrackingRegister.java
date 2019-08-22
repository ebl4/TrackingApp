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
    @SerializedName("auxiliarEntrada1")
    @Expose
    private String auxiliarEntrada1;
    @SerializedName("nomeEntrada2")
    @Expose
    private String nomeEntrada2;
    @SerializedName("valorEntrada2")
    @Expose
    private String valorEntrada2;
    @SerializedName("auxiliarEntrada2")
    @Expose
    private String auxiliarEntrada2;
    @SerializedName("nomeEntrada3")
    @Expose
    private String nomeEntrada3;
    @SerializedName("valorEntrada3")
    @Expose
    private String valorEntrada3;
    @SerializedName("auxiliarEntrada3")
    @Expose
    private String auxiliarEntrada3;

    public void setIdChamado(Integer idChamado) {
        this.idChamado = idChamado;
    }

    public void setNomeEntrada1(String nomeEntrada1) {
        this.nomeEntrada1 = nomeEntrada1;
    }

    public void setValorEntrada1(String valorEntrada1) {
        this.valorEntrada1 = valorEntrada1;
    }

    public void setAuxiliarEntrada1(String auxiliarEntrada1) {
        this.auxiliarEntrada1 = auxiliarEntrada1;
    }

    public void setNomeEntrada2(String nomeEntrada2) {
        this.nomeEntrada2 = nomeEntrada2;
    }

    public void setValorEntrada2(String valorEntrada2) {
        this.valorEntrada2 = valorEntrada2;
    }

    public void setAuxiliarEntrada2(String auxiliarEntrada2) {
        this.auxiliarEntrada2 = auxiliarEntrada2;
    }

    public void setNomeEntrada3(String nomeEntrada3) {
        this.nomeEntrada3 = nomeEntrada3;
    }

    public void setValorEntrada3(String valorEntrada3) {
        this.valorEntrada3 = valorEntrada3;
    }

    public void setAuxiliarEntrada3(String auxiliarEntrada3) {
        this.auxiliarEntrada3 = auxiliarEntrada3;
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

    public String getAuxiliarEntrada1() {
        return auxiliarEntrada1;
    }

    public String getNomeEntrada2() {
        return nomeEntrada2;
    }

    public String getValorEntrada2() {
        return valorEntrada2;
    }

    public String getAuxiliarEntrada2() {
        return auxiliarEntrada2;
    }

    public String getNomeEntrada3() {
        return nomeEntrada3;
    }

    public String getValorEntrada3() {
        return valorEntrada3;
    }

    public String getAuxiliarEntrada3() {
        return auxiliarEntrada3;
    }
}
