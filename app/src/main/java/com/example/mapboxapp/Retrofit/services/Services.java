package com.example.mapboxapp.Retrofit.services;


import com.example.mapboxapp.Tracking.Model.CidadeRegister;
import com.example.mapboxapp.Tracking.Model.Empresa;
import com.example.mapboxapp.Tracking.Model.EstadoRegister;
import com.example.mapboxapp.Tracking.Model.IdEstado;
import com.example.mapboxapp.Tracking.Model.Motivo;
import com.example.mapboxapp.Tracking.Model.TrackingRegister;
import com.example.mapboxapp.Tracking.Model.UserRegister;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by edson
 */

public interface Services {

    @GET("ObterEmpresasComEndereco")
    Call<UserRegister> getEmpresas();

    @POST("CriarChamado?idItemDeCatalogo=363&login=gbsadmin")
    Call<TrackingRegister> criarChamadoNavegacao(@Body Motivo motivo);

    @POST("CriarEmpresaComEndereco")
    Call<Void> criarEmpresa(@Body Empresa empresa);

    @GET("ObterListaDeEstados")
    Call<EstadoRegister> getEstado();

    @POST("ObterListaDeCidades")
    Call<CidadeRegister> getCidade(@Body IdEstado idEstado);

}
