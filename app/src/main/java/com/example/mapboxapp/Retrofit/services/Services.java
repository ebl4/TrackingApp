package com.example.mapboxapp.Retrofit.services;


import com.example.mapboxapp.Tracking.Model.CidadeRegister;
import com.example.mapboxapp.Tracking.Model.Empresa;
import com.example.mapboxapp.Tracking.Model.EstadoRegister;
import com.example.mapboxapp.Tracking.Model.FirstTrackingRegister;
import com.example.mapboxapp.Tracking.Model.IdEstado;
import com.example.mapboxapp.Tracking.Model.Motivo;
import com.example.mapboxapp.Tracking.Model.TrackingRegister;
import com.example.mapboxapp.Tracking.Model.TrackingResponseChamado;
import com.example.mapboxapp.Tracking.Model.UserRegister;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by edson
 */

public interface Services {

    @GET("ObterEmpresasComEndereco?comTraking=true")
    Call<UserRegister> getEmpresas();

    @POST("CriarChamado?idItemDeCatalogo=363&login=gbsadmin")
    Call<TrackingRegister> criarChamadoNavegacao(@Body Motivo motivo);

    @POST("AlterarEntradasDoChamado?login=gbsadmin")
    Call<TrackingResponseChamado> alterarChamadoNavegacao(@Body FirstTrackingRegister firstRegister);

    @POST("CriarEmpresaComEndereco")
    Call<Void> criarEmpresa(@Body Empresa empresa);

    @GET("ObterListaDeEstados")
    Call<EstadoRegister> getEstado();

    @POST("ObterListaDeCidades")
    Call<CidadeRegister> getCidade(@Body IdEstado idEstado);
}
