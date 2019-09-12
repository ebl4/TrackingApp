package com.example.mapboxapp.Retrofit.services;


import com.example.mapboxapp.Tracking.Model.CidadeRegister;
import com.example.mapboxapp.Tracking.Model.Empresa;
import com.example.mapboxapp.Tracking.Model.EstadoRegister;
import com.example.mapboxapp.Tracking.Model.FirstTrackingRegister;
import com.example.mapboxapp.Tracking.Model.IdEstado;
import com.example.mapboxapp.Tracking.Model.ReportParameters;
import com.example.mapboxapp.Tracking.Model.SecondTrackingRegister;
import com.example.mapboxapp.Tracking.Model.Motivo;
import com.example.mapboxapp.Tracking.Model.TrackingRegister;
import com.example.mapboxapp.Tracking.Model.TrackingReport;
import com.example.mapboxapp.Tracking.Model.TrackingResponseChamado;
import com.example.mapboxapp.Tracking.Model.UserRegister;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by edson
 */

public interface Services {

    @GET("ObterEmpresasComEndereco")
    Call<UserRegister> getEmpresas();

    @POST("CriarChamado?idItemDeCatalogo=363&login=gbsadmin")
    Call<TrackingRegister> criarChamadoNavegacao(@Body Motivo motivo);

    @POST("AlterarEntradasDoChamado?login=gbsadmin")
    Call<TrackingResponseChamado> alterarChamadoNavegacao(@Body FirstTrackingRegister firstRegister);

    @POST("AlterarEntradasDoChamado?login=gbsadmin")
    Call<TrackingResponseChamado> alterarChamadoMenorRota(@Body SecondTrackingRegister register);

    @POST("ObterVisitasDoDeslocamento")
    Call<TrackingReport> obterVisitasDoDeslocamento(@Body ReportParameters parameters);

    @POST("CriarEmpresaComEndereco")
    Call<Void> criarEmpresa(@Body Empresa empresa);

    @GET("ObterListaDeEstados")
    Call<EstadoRegister> getEstado();

    @POST("ObterListaDeCidades")
    Call<CidadeRegister> getCidade(@Body IdEstado idEstado);
}
