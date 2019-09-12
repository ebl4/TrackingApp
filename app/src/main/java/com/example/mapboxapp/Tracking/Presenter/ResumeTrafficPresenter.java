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
import com.example.mapboxapp.Tracking.Model.SecondTrackingRegister;
import com.example.mapboxapp.Tracking.Model.Motivo;
import com.example.mapboxapp.Tracking.Model.TrackingRegister;
import com.example.mapboxapp.Tracking.Model.TrackingResponseChamado;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfigInt;
import com.example.mapboxapp.Tracking.Utils.PreferencesManager;
import com.example.mapboxapp.Tracking.Utils.PreferencesManagerInt;
import com.example.mapboxapp.Tracking.View.ResumeTrafficView;
import com.example.mapboxapp.Tracking.View.ResumeTrafficViewInt;
import com.example.mapboxapp.Tracking.View.Fragments.VisitFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that implements and format tracking information, such as distance traveled, tracking time
 * and store data in the Web Service or save data offline
 */
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

    /**
     * Receives data from navigation such as destination and distance traveled and set them for
     * save in the Web Service
     * @param option
     * @param params
     * @return Second Tracking Regiter for SSM Web Service
     */
    public SecondTrackingRegister formatMinRouteRegister(int option, String...params){
        SecondTrackingRegister register = new SecondTrackingRegister();
        FirstTrackingRegister firstTrackingRegister = this.formatFirstRegiter(option, params);
        register.setData(firstTrackingRegister);
        register.setPosicaoEntrada3(6);
        return register;
    }

    /**
     * Set each of the navigation information according with option that
     * indicates the part of the SSM Web Service register
     * @param option
     * @param params
     * @return FirstTrackingRegister a type of register for Web Service
     */
    public FirstTrackingRegister formatFirstRegiter(int option, String...params){
        String[] splitResult;
        FirstTrackingRegister register = new FirstTrackingRegister();
        register.setNomeEntrada1(params[0]);
        register.setNomeEntrada2(params[1]);
        register.setNomeEntrada3(params[2]);
        switch (option){
            case 1:
                register.setValorEntrada1(prefConfig.getString(context.getString(R.string.partida)));
                register.setValorAuxiliarEntrada1(prefConfig.getString(context.getString(R.string.partida)));

                register.setValorEntrada2(prefConfig.getString(context.getString(R.string.partida)));
                register.setValorAuxiliarEntrada2(prefConfig.getString(context.getString(R.string.partida)));

                register.setValorEntrada3(prefConfig.getString(context.getString(R.string.clientName)));
                register.seValorAuxiliarEntrada3(prefConfig.getString(context.getString(R.string.clientName)));
                break;
            case 2:
                register.setValorEntrada1(prefConfig.getString(context.getString(R.string.destino)));
                register.setValorAuxiliarEntrada1(prefConfig.getString(context.getString(R.string.destino)));

                splitResult = prefConfig.getString(context.getString(R.string.distanceTraveled)).split(" ");
                register.setValorEntrada2(splitResult[0]);
                register.setValorAuxiliarEntrada2(splitResult[1]);
                splitResult = prefConfig.getString(context.getString(R.string.resumeTime)).split(" ");
                register.setValorEntrada3(splitResult[0]);
                register.seValorAuxiliarEntrada3(splitResult[1]);
                break;
            case 3:
                register.setValorEntrada1(prefConfig.getString(context.getString(R.string.minRouteAddress)));
                register.setValorAuxiliarEntrada1(prefConfig.getString(context.getString(R.string.minRouteAddress)));

                splitResult = prefConfig.getString(context.getString(R.string.minRouteDistance)).split(" ");
                register.setValorEntrada2(splitResult[0]);
                register.setValorAuxiliarEntrada2(splitResult[1]);

                splitResult = prefConfig.getString(context.getString(R.string.minRouteTime)).split(" ");
                register.setValorEntrada3(splitResult[0]);
                register.seValorAuxiliarEntrada3(splitResult[1]);
                break;
        }

        return register;
    }

    /**
     * Format all navigation fields related to tracking activity
     * to show in the view
     * @param views
     */
    public void formatFields(TextView...views){
        String motivoCancelamento = prefConfig.getString(context.getString(R.string.motivoCancelamento));
        String status = prefConfig.getString(context.getString(R.string.navigationStatus));
        views[0].setText(prefConfig.getString(context.getString(R.string.distanceTraveled)));
        views[1].setText(formatDuration(prefConfig.getString(context.getString(R.string.resumeTime))));
        views[2].setText(prefConfig.getString(context.getString(R.string.partida)));
        views[3].setText(prefConfig.getString(context.getString(R.string.destino)));

        if(status.equalsIgnoreCase(context.getString(R.string.viagemCancelada)) && !motivoCancelamento.isEmpty()){
            status = status + " - " + motivoCancelamento;
        }
        views[4].setText(status);
    }

    @Override
    public void saveData(){
        view.showDialog();

        Services service = RetrofitConfig.getRetrofitInstance(VisitFragment.CLIENTS_URL).create(Services.class);
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
                    firstRegister.setIdChamado(response.body().getIdChamado());
                    lastRegister.setIdChamado(response.body().getIdChamado());
                    secondRegister.setIdChamado(response.body().getIdChamado());
                    saveFirstDataChamado(firstRegister, service);
                    saveFirstDataChamado(lastRegister, service);
                    saveMenorRotaChamado(secondRegister, service);
                    view.hideDialog();
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

                view.hideDialog();
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

    public String formatDuration(String duration){
        return duration.replace("mm", "min")
                .replace("hh", "h")
                .replace("ss", "seg");
    }

}
