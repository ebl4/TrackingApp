package com.example.mapboxapp.Tracking.Presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Retrofit.RetrofitConfig;
import com.example.mapboxapp.Retrofit.services.Services;
import com.example.mapboxapp.Tracking.Database.TrackingContract;
import com.example.mapboxapp.Tracking.Database.TrackingDbHelper;
import com.example.mapboxapp.Tracking.Model.FirstTrackingRegister;
import com.example.mapboxapp.Tracking.Model.SecondTrackingRegister;
import com.example.mapboxapp.Tracking.Model.Motivo;
import com.example.mapboxapp.Tracking.Model.OfflineNavigationData;
import com.example.mapboxapp.Tracking.Model.TrackingRegister;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfigInt;
import com.example.mapboxapp.Tracking.View.Fragments.VisitFragment;
import com.example.mapboxapp.Tracking.View.Fragments.VisitFragmentInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.mapboxapp.Tracking.Database.TrackingContract.CustomerEntry.COLUMN_NAME_CUSTOMER_ADDRESS;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.CustomerEntry.COLUMN_NAME_CUSTOMER_NAME;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.TrackingEntry.COLUMN_NAME_CLIENT_NAME;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.TrackingEntry.COLUMN_NAME_DESTINO;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.TrackingEntry.COLUMN_NAME_DISTANCIA;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.TrackingEntry.COLUMN_NAME_MENOR_ROTA_DISTANCIA;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.TrackingEntry.COLUMN_NAME_MENOR_ROTA_ENDERECO;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.TrackingEntry.COLUMN_NAME_MENOR_ROTA_TEMPO;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.TrackingEntry.COLUMN_NAME_MOTIVO_VIAGEM;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.TrackingEntry.COLUMN_NAME_ORIGEM;
import static com.example.mapboxapp.Tracking.Database.TrackingContract.TrackingEntry.COLUMN_NAME_TEMPO_VIAGEM;
import static com.example.mapboxapp.Tracking.Presenter.ResumeTrafficPresenter.saveFirstDataChamado;
import static com.example.mapboxapp.Tracking.Presenter.ResumeTrafficPresenter.saveMenorRotaChamado;

/**
 * Class that format infomation for visit activity and
 * treat offline data related to tracking that has not saved yet
 */
public class VisitPresenter implements VisitPresenterInt {

    /**
     * Variables for local database and offline data
     */
    List<OfflineNavigationData> offlineData;
    private SQLiteDatabase db;
    private TrackingDbHelper dbHelper;
    private boolean updateCustomer;
    PreferenceConfigInt prefs;

    //Variables for view interface
    private VisitFragmentInt view;
    private Context context;

    public VisitPresenter(Context context, VisitFragmentInt view){
        this.view = view;
        this.context = context;
        dbHelper = new TrackingDbHelper(context);
        db = dbHelper.getReadableDatabase();
        dbHelper.onCreate(db);
        prefs = new PreferenceConfig(context);
    }

    @Override
    public void retriveOfflineData() {
        String[] projection = {
                BaseColumns._ID,
                COLUMN_NAME_CLIENT_NAME,
                COLUMN_NAME_ORIGEM,
                COLUMN_NAME_DESTINO,
                COLUMN_NAME_DISTANCIA,
                COLUMN_NAME_TEMPO_VIAGEM,
                COLUMN_NAME_MOTIVO_VIAGEM,
                COLUMN_NAME_MENOR_ROTA_ENDERECO,
                COLUMN_NAME_MENOR_ROTA_DISTANCIA,
                COLUMN_NAME_MENOR_ROTA_TEMPO
        };
        String sortOrder =
                COLUMN_NAME_CLIENT_NAME + " DESC";
        Cursor cursor = db.query(
                TrackingContract.TrackingEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);

        offlineData = new ArrayList<OfflineNavigationData>();
        while(cursor.moveToNext()) {
            offlineData.add(new OfflineNavigationData(cursor.getString(
                    cursor.getColumnIndexOrThrow(TrackingContract.TrackingEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_CLIENT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ORIGEM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DESTINO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DISTANCIA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TEMPO_VIAGEM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_MOTIVO_VIAGEM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_MENOR_ROTA_ENDERECO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_MENOR_ROTA_DISTANCIA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_MENOR_ROTA_TEMPO))
            ));
        }
        cursor.close();
    }

    @Override
    public boolean checkOfflineData() {
        boolean hasOfflineData = false;
        String[] projection = {
                BaseColumns._ID,
                COLUMN_NAME_CLIENT_NAME,
        };
        String sortOrder =
                COLUMN_NAME_CLIENT_NAME + " DESC";
        Cursor cursor = db.query(
                TrackingContract.TrackingEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);
        if(cursor.moveToNext()) {
            hasOfflineData = true;
        }
        cursor.close();
        return hasOfflineData;
    }

    @Override
    public void saveData(){

        //para cada offlineNavigationData
        retriveOfflineData();

        for (OfflineNavigationData offlineItem:
                offlineData) {

            Motivo motivo = new Motivo();
            motivo.setMotivo(offlineItem.get_COLUMN_NAME_MOTIVO_VIAGEM());
            Services service = RetrofitConfig.getRetrofitInstance(VisitFragment.CLIENTS_URL).create(Services.class);

            FirstTrackingRegister firstRegister =
                    this.formatFirstRegiter(1, "Origem (Cliente)", "Origem (Endereço)", "Destino (Cliente)",
                            offlineItem.get_COLUMN_NAME_ORIGEM(), offlineItem.get_COLUMN_NAME_CLIENT_NAME());
            SecondTrackingRegister minRouteRegister =
                    this.formatMinRouteRegister(2, "Destino (Endereço)", "Deslocamento (Km)", "Tempo",
                            offlineItem.get_COLUMN_NAME_DESTINO(), offlineItem.get_COLUMN_NAME_DISTANCIA(),
                            offlineItem.get_COLUMN_NAME_TEMPO_VIAGEM());
            FirstTrackingRegister lastRegister =
                    this.formatFirstRegiter(3, "Menor Rota (Endereço)", "Menor Rota (KM)", "Menor Rota (Tempo)",
                            offlineItem.get_COLUMN_NAME_MENOR_ROTA_ENDERECO(), offlineItem.get_COLUMN_NAME_MENOR_ROTA_DISTANCIA(),
                            offlineItem.get_COLUMN_NAME_MENOR_ROTA_TEMPO());

            Call<TrackingRegister> call = service.criarChamadoNavegacao(motivo);
            call.enqueue(new Callback<TrackingRegister>() {
                @Override
                public void onResponse(Call<TrackingRegister> call, Response<TrackingRegister> response) {
                    view.showNavigationSuccess("Dados de navegação gravados com sucesso. Chamado: "
                            + response.body().getIdChamado());

                    firstRegister.setIdChamado(response.body().getIdChamado());
                    lastRegister.setIdChamado(response.body().getIdChamado());
                    minRouteRegister.setIdChamado(response.body().getIdChamado());
                    saveFirstDataChamado(firstRegister, service);
                    saveFirstDataChamado(lastRegister, service);
                    saveMenorRotaChamado(minRouteRegister, service);

                    //exclui os dados do banco local
                    String selection = COLUMN_NAME_CLIENT_NAME + " LIKE ?";
                    String[] selectionArgs = { offlineItem.get_COLUMN_NAME_CLIENT_NAME() };

                    db.delete(TrackingContract.TrackingEntry.TABLE_NAME, selection, selectionArgs);
                }

                @Override
                public void onFailure(Call<TrackingRegister> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void retriveCustomerOfflineData(HashMap<String, String> clients, List<String> clientNames) {
        String[] projection = {
                BaseColumns._ID,
                COLUMN_NAME_CUSTOMER_NAME,
                COLUMN_NAME_CUSTOMER_ADDRESS
        };
        String sortOrder =
                COLUMN_NAME_CUSTOMER_NAME + " DESC", clientName;
        Cursor cursor = db.query(
                TrackingContract.CustomerEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);

        while(cursor.moveToNext()) {
            clientName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_CUSTOMER_NAME));
            clientNames.add(clientName);
            clients.put(clientName, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_CUSTOMER_ADDRESS)));
        }
        cursor.close();
    }

    @Override
    public void updateOfflineCustomerData(HashMap<String, String> clients, List<String> clientNames, boolean synchronize){
        ContentValues values = new ContentValues();
        updateCustomer = prefs.getBoolean(context.getString(R.string.enterpriseUpdated));

        if(!checkCustomerOfflineData() || synchronize || updateCustomer) {
            //save offline data

            //client data
            for (int i = 0; i < clientNames.size() && i < 100; i++) {
                values.put(TrackingContract.CustomerEntry.COLUMN_NAME_CUSTOMER_NAME, clientNames.get(i));
                values.put(TrackingContract.CustomerEntry.COLUMN_NAME_CUSTOMER_ADDRESS, clients.get(clientNames.get(i)));
                long newRowId = db.insert(TrackingContract.CustomerEntry.TABLE_NAME, null, values);
            }
            updateCustomer = false;
            prefs.putBoolean(context.getString(R.string.enterpriseUpdated), updateCustomer);
        }
    }

    @Override
    public boolean checkCustomerOfflineData() {
        boolean hasOfflineData = false;
        String[] projection = {
                BaseColumns._ID,
                COLUMN_NAME_CUSTOMER_NAME,
        };
        String sortOrder =
                COLUMN_NAME_CUSTOMER_NAME + " DESC";
        Cursor cursor = db.query(
                TrackingContract.CustomerEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);
        if(cursor.moveToNext()) {
            hasOfflineData = true;
        }
        cursor.close();
        return hasOfflineData;
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
                register.setValorEntrada1(params[3]);
                register.setValorAuxiliarEntrada1(params[3]);

                register.setValorEntrada2(params[3]);
                register.setValorAuxiliarEntrada2(params[3]);

                register.setValorEntrada3(params[4]);
                register.seValorAuxiliarEntrada3(params[4]);
                break;
            case 2:
                register.setValorEntrada1(params[3]);
                register.setValorAuxiliarEntrada1(params[3]);

                register.setValorEntrada2(params[4]);
                register.setValorAuxiliarEntrada2(params[4]);

                register.setValorEntrada3(params[5]);
                register.seValorAuxiliarEntrada3(params[5]);
                break;
            case 3:
                register.setValorEntrada1(params[3]);
                register.setValorAuxiliarEntrada1(params[3]);

                register.setValorEntrada2(params[4]);
                register.setValorAuxiliarEntrada2(params[4]);

                register.setValorEntrada3(params[5]);
                register.seValorAuxiliarEntrada3(params[5]);
                break;
        }

        return register;
    }

}
