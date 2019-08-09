package com.example.mapboxapp.Tracking.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DadoCidade {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Nome")
    @Expose
    private String nome;
    @SerializedName("Sigla")
    @Expose
    private String sigla;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

}

