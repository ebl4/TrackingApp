package com.example.mapboxapp.Tracking.Presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Retrofit.RetrofitConfig;
import com.example.mapboxapp.Retrofit.services.Services;
import com.example.mapboxapp.Tracking.Database.TrackingContract;
import com.example.mapboxapp.Tracking.Database.TrackingDbHelper;
import com.example.mapboxapp.Tracking.Model.FirstTrackingRegister;
import com.example.mapboxapp.Tracking.Model.ResumeTrafficViewInt;
import com.example.mapboxapp.Tracking.Model.SecondTrackingRegister;
import com.example.mapboxapp.Tracking.Model.Motivo;
import com.example.mapboxapp.Tracking.Model.TrackingRegister;
import com.example.mapboxapp.Tracking.Model.TrackingResponseChamado;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfigInt;
import com.example.mapboxapp.Tracking.Utils.PreferencesManager;
import com.example.mapboxapp.Tracking.Utils.PreferencesManagerInt;
import com.example.mapboxapp.Tracking.View.ResumeTrafficView;
import com.example.mapboxapp.Tracking.View.VisitView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumeTrafficPresenter implements ResumeTrafficPresenterInt {

    private PreferenceConfigInt prefConfig;
    private PreferencesManagerInt prefs;
    private Context context;
    private ResumeTrafficViewInt view;

    public ResumeTrafficPresenter(Context context, ResumeTrafficView view){
        this.view = view;
        this.context = context;
        prefConfig = new PreferenceConfig(context);
        prefs = new PreferencesManager(context);
    }

    @Override
    public String formatMotivo(){
        return context.getString(R.string.motivoViagem) + ": " + prefs.getString(context.getString(R.string.motivoViagem), 1);
    }

    public SecondTrackingRegister formatMinRouteRegister(int option, String...params){
        SecondTrackingRegister register = new SecondTrackingRegister();
        FirstTrackingRegister firstTrackingRegister = this.formatFirstRegiter(option, params);
        register.setData(firstTrackingRegister);
        register.setPosicaoEntrada3(5);
        return register;
    }

    public FirstTrackingRegister formatFirstRegiter(int option, String...params){
        FirstTrackingRegister register = new FirstTrackingRegister();
        register.setNomeEntrada1(params[0]);
        register.setNomeEntrada2(params[1]);
        register.setNomeEntrada3(params[2]);
        switch (option){
            case 1:
                register.setValorEntrada1(prefConfig.getString(context.getString(R.string.partida)));
                register.setAuxiliarEntrada1(prefConfig.getString(context.getString(R.string.partida)));

                register.setValorEntrada2(prefConfig.getString(context.getString(R.string.partida)));
                register.setAuxiliarEntrada2(prefConfig.getString(context.getString(R.string.partida)));

                register.setValorEntrada3(prefConfig.getString(context.getString(R.string.clientName)));
                register.setAuxiliarEntrada3(prefConfig.getString(context.getString(R.string.clientName)));
                break;
            case 2:
                register.setValorEntrada1(prefConfig.getString(context.getString(R.string.destino)));
                register.setAuxiliarEntrada1(prefConfig.getString(context.getString(R.string.destino)));

                register.setValorEntrada2(prefConfig.getString(context.getString(R.string.distanceTraveled)));
                register.setAuxiliarEntrada2(prefConfig.getString(context.getString(R.string.distanceTraveled)));

                register.setValorEntrada3(prefConfig.getString(context.getString(R.string.resumeTime)));
                register.setAuxiliarEntrada3(prefConfig.getString(context.getString(R.string.resumeTime)));
                break;
            case 3:
                register.setValorEntrada1(prefConfig.getString(context.getString(R.string.minRouteAddress)));
                register.setAuxiliarEntrada1(prefConfig.getString(context.getString(R.string.minRouteAddress)));

                register.setValorEntrada2(prefConfig.getString(context.getString(R.string.minRouteDistance)));
                register.setAuxiliarEntrada2(prefConfig.getString(context.getString(R.string.minRouteDistance)));

                register.setValorEntrada3(prefConfig.getString(context.getString(R.string.minRouteTime)));
                register.setAuxiliarEntrada3(prefConfig.getString(context.getString(R.string.minRouteTime)));
                break;
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
        Services service = RetrofitConfig.getRetrofitInstance(VisitView.CLIENTS_URL).create(Services.class);
        Motivo motivo = new Motivo();
        FirstTrackingRegister firstRegister =
                this.formatFirstRegiter(1, "Origem (Cliente)", "Origem (Endereço)", "Destino (Cliente)");
        SecondTrackingRegister secondRegister =
                this.formatMinRouteRegister(2, "Destino (Endereço)", "Deslocamento (Km)", "Tempo");
        FirstTrackingRegister lastRegister =
                this.formatFirstRegiter(3, "Menor Rota (Endereço)", "Menor Rota (KM)", "Menor Rota (Tempo)");

        motivo.setMotivo(formatMotivo());

        Call<TrackingRegister> call = service.criarChamadoNavegacao(motivo);
        call.enqueue(new Callback<TrackingRegister>() {
            @Override
            public void onResponse(Call<TrackingRegister> call, Response<TrackingRegister> response) {
                if(response.body() != null){
                    view.showNavigationSuccess("Dados de navegação gravados com sucesso. Chamado: "
                            + response.body().getIdChamado());
//                    Toast.makeText(context, "Dados de navegação gravados com sucesso. Chamado: "
//                            + response.body().getIdChamado(), Toast.LENGTH_SHORT).show();
                    firstRegister.setIdChamado(response.body().getIdChamado());
                    lastRegister.setIdChamado(response.body().getIdChamado());
                    secondRegister.setIdChamado(response.body().getIdChamado());
                    saveFirstDataChamado(firstRegister, service);
                    saveFirstDataChamado(lastRegister, service);
                    saveMenorRotaChamado(secondRegister, service);
                }
            }
            @Override
            public void onFailure(Call<TrackingRegister> call, Throwable t) {
                Toast.makeText(context, "Erro ao gravar dados de navegação", Toast.LENGTH_SHORT).show();
                //local database storage
                TrackingDbHelper dbHelper = new TrackingDbHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                //dados referentes à navegação
                values.put(TrackingContract.TrackingEntry.COLUMN_NAME_CLIENT_NAME, firstRegister.getValorEntrada3());
                values.put(TrackingContract.TrackingEntry.COLUMN_NAME_ORIGEM, firstRegister.getValorEntrada1());
                values.put(TrackingContract.TrackingEntry.COLUMN_NAME_DESTINO, secondRegister.getValorEntrada1());
                values.put(TrackingContract.TrackingEntry.COLUMN_NAME_DISTANCIA, secondRegister.getValorEntrada2());
                values.put(TrackingContract.TrackingEntry.COLUMN_NAME_TEMPO_VIAGEM, secondRegister.getValorEntrada3());
                values.put(TrackingContract.TrackingEntry.COLUMN_NAME_MOTIVO_VIAGEM, prefs.getString(context.getString(R.string.motivoViagem), 1));

                //dados da menor rota
                values.put(TrackingContract.TrackingEntry.COLUMN_NAME_MENOR_ROTA_ENDERECO, lastRegister.getValorEntrada1());
                values.put(TrackingContract.TrackingEntry.COLUMN_NAME_MENOR_ROTA_DISTANCIA, lastRegister.getValorEntrada2());
                values.put(TrackingContract.TrackingEntry.COLUMN_NAME_MENOR_ROTA_TEMPO, lastRegister.getValorEntrada3());

                long newRowId = db.insert(TrackingContract.TrackingEntry.TABLE_NAME, null, values);
            }
        });
    }

    public static void saveFirstDataChamado(FirstTrackingRegister register, Services service){

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

    public static void saveMenorRotaChamado(SecondTrackingRegister register, Services service){

        Call<TrackingResponseChamado> call = service.alterarChamadoMenorRota(register);
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
