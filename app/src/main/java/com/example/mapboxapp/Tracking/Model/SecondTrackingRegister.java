package com.example.mapboxapp.Tracking.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SecondTrackingRegister extends FirstTrackingRegister {

    @SerializedName("posicaoEntrada3")
    @Expose
    private Integer posicaoEntrada3;

    public void setPosicaoEntrada3(Integer posicaoEntrada3) {
        this.posicaoEntrada3 = posicaoEntrada3;
    }

    public Integer getPosicaoEntrada3() {
        return posicaoEntrada3;
    }

    public void setData(FirstTrackingRegister register){
        this.setNomeEntrada1(register.getNomeEntrada1());
        this.setNomeEntrada2(register.getNomeEntrada2());
        this.setNomeEntrada3(register.getNomeEntrada3());
        this.setAuxiliarEntrada1(register.getAuxiliarEntrada1());
        this.setAuxiliarEntrada2(register.getAuxiliarEntrada2());
        this.setAuxiliarEntrada3(register.getAuxiliarEntrada3());
        this.setValorEntrada1(register.getValorEntrada1());
        this.setValorEntrada2(register.getValorEntrada2());
        this.setValorEntrada3(register.getValorEntrada3());
    }
}
