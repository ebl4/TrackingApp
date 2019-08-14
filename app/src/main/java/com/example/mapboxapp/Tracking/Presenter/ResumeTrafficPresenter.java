package com.example.mapboxapp.Tracking.Presenter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Retrofit.RetrofitConfig;
import com.example.mapboxapp.Retrofit.services.Services;
import com.example.mapboxapp.Tracking.Model.FirstTrackingRegister;
import com.example.mapboxapp.Tracking.Model.Motivo;
import com.example.mapboxapp.Tracking.Model.TrackingRegister;
import com.example.mapboxapp.Tracking.Model.TrackingResponseChamado;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfigInt;
import com.example.mapboxapp.Tracking.Utils.PreferencesManager;
import com.example.mapboxapp.Tracking.Utils.PreferencesManagerInt;
import com.example.mapboxapp.Tracking.View.VisitActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumeTrafficPresenter implements ResumeTrafficPresenterInt {

    private PreferenceConfigInt prefConfig;
    private PreferencesManagerInt prefs;
    private Context context;

    public ResumeTrafficPresenter(Context context){
        prefConfig = new PreferenceConfig(context);
        prefs = new PreferencesManager(context);
        this.context = context;
    }

    @Override
    public String formatMotivo(){
        return context.getString(R.string.motivoViagem) + ": " + prefs.getString(context.getString(R.string.motivoViagem), 1);
    }

    public FirstTrackingRegister formatFirstRegiter(boolean first, String...params){
        FirstTrackingRegister register = new FirstTrackingRegister();
        register.setNomeEntrada1(params[0]);
        register.setNomeEntrada2(params[1]);
        register.setNomeEntrada3(params[2]);
        if(first){
            register.setValorEntrada1(prefConfig.getString(context.getString(R.string.partida)));
            register.setAuxiliarEntrada1(prefConfig.getString(context.getString(R.string.partida)));

            register.setValorEntrada2(prefConfig.getString(context.getString(R.string.partida)));
            register.setAuxiliarEntrada2(prefConfig.getString(context.getString(R.string.partida)));

            register.setValorEntrada3(prefConfig.getString(context.getString(R.string.clientName)));
            register.setAuxiliarEntrada3(prefConfig.getString(context.getString(R.string.clientName)));
        }
        else{
            register.setValorEntrada1(prefConfig.getString(context.getString(R.string.destino)));
            register.setAuxiliarEntrada1(prefConfig.getString(context.getString(R.string.destino)));

            register.setValorEntrada2(prefConfig.getString(context.getString(R.string.distanceTraveled)));
            register.setAuxiliarEntrada2(prefConfig.getString(context.getString(R.string.distanceTraveled)));

            register.setValorEntrada3(prefConfig.getString(context.getString(R.string.resumeTime)));
            register.setAuxiliarEntrada3(prefConfig.getString(context.getString(R.string.resumeTime)));
        }

        return register;
    }

    public void formatFields(TextView...views){
        String motivoCancelamento = prefConfig.getString(context.getString(R.string.motivoCancelamento));
        String status = prefConfig.getString(context.getString(R.string.navigationStatus));
        String motivo;
        views[0].setText(prefConfig.getString(context.getString(R.string.distanceTraveled)));
        views[1].setText(prefConfig.getString(context.getString(R.string.resumeTime)));
        views[2].setText(prefConfig.getString(context.getString(R.string.partida)));
        views[3].setText(prefConfig.getString(context.getString(R.string.destino)));

        if(status.equalsIgnoreCase(context.getString(R.string.viagemCancelada)) && !motivoCancelamento.isEmpty()){
            status = status + " - " + motivoCancelamento;
        }
        views[4].setText(status);
    }

    @Override
    public void saveData(){
        Services service = RetrofitConfig.getRetrofitInstance(VisitActivity.CLIENTS_URL).create(Services.class);
        Motivo motivo = new Motivo();
        FirstTrackingRegister firstRegister =
                this.formatFirstRegiter(true, "Origem (Cliente)", "Origem (Endereço)", "Destino (Cliente)");
        FirstTrackingRegister lastRegister =
                this.formatFirstRegiter(false, "Destino (Endereço)", "Deslocamento", "Tempo");
        motivo.setMotivo(formatMotivo());

        Call<TrackingRegister> call = service.criarChamadoNavegacao(motivo);
        call.enqueue(new Callback<TrackingRegister>() {
            @Override
            public void onResponse(Call<TrackingRegister> call, Response<TrackingRegister> response) {
                if(response.body() != null){
                    Toast.makeText(context, "Dados de navegação gravados com sucesso. Chamado: "
                            + response.body().getIdChamado(), Toast.LENGTH_SHORT).show();
                    firstRegister.setIdChamado(response.body().getIdChamado());
                    lastRegister.setIdChamado(response.body().getIdChamado());
                    saveFirstDataChamado(firstRegister, service);
                    saveFirstDataChamado(lastRegister, service);
                }
            }
            @Override
            public void onFailure(Call<TrackingRegister> call, Throwable t) {
                Toast.makeText(context, "Erro ao gravar dados de navegação", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveFirstDataChamado(FirstTrackingRegister register, Services service){

        Call<TrackingResponseChamado> call = service.alterarChamadoNavegacao(register);
        call.enqueue(new Callback<TrackingResponseChamado>() {
            @Override
            public void onResponse(Call<TrackingResponseChamado> call, Response<TrackingResponseChamado> response) {
                if(response.body() != null){
//                    Toast.makeText(context, "Dados de navegação para o chamado gravados com sucesso",
//                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TrackingResponseChamado> call, Throwable t) {
                //Toast.makeText(context, "Erro ao gravar dados de navegação para o chamado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
