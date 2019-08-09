package com.example.mapboxapp.Tracking.Presenter;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Retrofit.RetrofitConfig;
import com.example.mapboxapp.Retrofit.services.Services;
import com.example.mapboxapp.Tracking.Model.Motivo;
import com.example.mapboxapp.Tracking.Model.TrackingRegister;
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
        StringBuilder motivo = new StringBuilder();
        motivo.append(context.getString(R.string.distanceTraveled)).append(": ").append(prefConfig.getString(context.getString(R.string.distanceTraveled)))
                .append("/")
                .append(context.getString(R.string.resumeTime)).append(": ").append(prefConfig.getString(context.getString(R.string.resumeTime)))
                .append("/")
                .append(context.getString(R.string.partida)).append(": ").append(prefConfig.getString(context.getString(R.string.partida)))
                .append("/")
                .append(context.getString(R.string.destino)).append(": ").append(prefConfig.getString(context.getString(R.string.destino)))
                .append("/")
                .append(context.getString(R.string.motivoViagem)).append(": ").append(prefs.getString(context.getString(R.string.motivoViagem), 1));
        return motivo.toString();
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
        motivo.setMotivo(formatMotivo());

        Call<TrackingRegister> call = service.criarChamadoNavegacao(motivo);
        call.enqueue(new Callback<TrackingRegister>() {
            @Override
            public void onResponse(Call<TrackingRegister> call, Response<TrackingRegister> response) {
                if(response.body() != null){
                    Toast.makeText(context, "Dados de navegação gravados com sucesso. Chamado: "
                            + response.body().getIdChamado(), Toast.LENGTH_SHORT).show();
                    prefConfig.clearPreferences();
                }
            }
            @Override
            public void onFailure(Call<TrackingRegister> call, Throwable t) {
                Toast.makeText(context, "Erro ao gravar dados de navegação", Toast.LENGTH_SHORT).show();
                //offline storage

            }
        });
    }
}
