package com.example.mapboxapp.Tracking.Presenter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Retrofit.RetrofitConfig;
import com.example.mapboxapp.Retrofit.services.Services;
import com.example.mapboxapp.Tracking.Model.CidadeRegister;
import com.example.mapboxapp.Tracking.Model.DadoCidade;
import com.example.mapboxapp.Tracking.Model.DadoEstado;
import com.example.mapboxapp.Tracking.Model.Empresa;
import com.example.mapboxapp.Tracking.Model.EstadoRegister;
import com.example.mapboxapp.Tracking.Model.IdEstado;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfigInt;
import com.example.mapboxapp.Tracking.View.RegisterEnterpriseActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterEnterprisePresenter implements RegisterEnterprisePresenterInt {
    private RegisterEnterpriseActivity view;
    private Context context;
    private ArrayList<String[]> arrayEstado;
    private ArrayList<String[]> arrayCidade;
    private PreferenceConfigInt prefs;

    public RegisterEnterprisePresenter(RegisterEnterpriseActivity view, Context context) {
        this.view = view;
        this.context = context;
        prefs = new PreferenceConfig(context);
    }

    @Override
    public void makeRequest(Empresa empresa) {
        Services service = RetrofitConfig.getRetrofitInstance(RegisterEnterpriseActivity.RESOURCES_URL).create(Services.class);
        Call<Void> call = service.criarEmpresa(empresa);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    //salvo com sucesso
                    Toast.makeText(context, "Empresa salva com sucesso", Toast.LENGTH_SHORT).show();
                    //nova empresa a ser inserida no banco offline
                    prefs.putBoolean(context.getString(R.string.enterpriseUpdated), true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Falha ao salvar a empresa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean checkTextFields(TextInputLayout... fields) {
        boolean fieldsOk = false;
        if(fields[0].getEditText().getText().toString().length() <= 3){
            fields[0].setError("Por favor digite um nome válido");
            fields[0].requestFocus();
        }
        else if(fields[1].getEditText().getText().toString().length() <= 3){
            fields[1].setError("Por favor digite um cep válido");
            fields[1].requestFocus();
        }
        else if(fields[2].getEditText().getText().toString().length() <= 3){
            fields[2].setError("Por favor digite um logradouro válido");
            fields[2].requestFocus();
        }
        else if(fields[3].getEditText().getText().toString().length() <= 3){
            fields[3].setError("Por favor digite um bairro válido");
            fields[3].requestFocus();
        }
        else if(fields[4].getEditText().getText().toString().length() <= 0){
            fields[4].setError("Por favor digite um número válido");
            fields[4].requestFocus();
        }
        else {
            fieldsOk = true;
        }
        return fieldsOk;
    }

    @Override
    public void getEstados() {
        Services service = RetrofitConfig.getRetrofitInstance(RegisterEnterpriseActivity.RESOURCES_URL).create(Services.class);
        Call<EstadoRegister> call = service.getEstado();
        Log.wtf("UrlEstado", call.request().url().toString());

        call.enqueue(new Callback<EstadoRegister>() {
            @Override
            public void onResponse(@NonNull Call<EstadoRegister> call, @NonNull Response<EstadoRegister> response) {
                if (response.body() != null) {
                    List<String> estadoSigla = new ArrayList<>();
                    List<DadoEstado> estadoList = response.body().getDados();
                    arrayEstado = new ArrayList<>();
                    for (int i = 0; i < estadoList.size(); i++) {
                        String[] estadoVector = new String[3];
                        estadoVector[0] = String.valueOf(estadoList.get(i).getId());
                        estadoVector[1] = estadoList.get(i).getNome();
                        estadoVector[2] = estadoList.get(i).getSigla();
                        arrayEstado.add(estadoVector);
                        estadoSigla.add(estadoList.get(i).getSigla());
                    }
                    view.formatEstadoAdapter(estadoSigla);

                } else {
                    view.showErrorRequest(context.getString(R.string.fail_load_states));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EstadoRegister> call, @NonNull Throwable t) {
                view.showErrorRequest(context.getString(R.string.fail_load_states));
            }
        });
    }

    @Override
    public void setUpCidadeSpinner(int position) {
        Log.d("TesteCidadeClick2", arrayEstado.get(position)[1]);
        Services service = RetrofitConfig.getRetrofitInstance(RegisterEnterpriseActivity.RESOURCES_URL).create(Services.class);
        Call<CidadeRegister> call = service.getCidade(new IdEstado(arrayEstado.get(position)[0]));
        Log.wtf("UrlCidade", call.request().url().toString());

        call.enqueue(new Callback<CidadeRegister>() {
            @Override
            public void onResponse(@NonNull Call<CidadeRegister> call, @NonNull Response<CidadeRegister> response) {
                if (response.body() != null) {
                    List<String> cidadeName = new ArrayList<>();
                    List<DadoCidade> cidadeList = response.body().getDados();
                    arrayCidade = new ArrayList<>();
                    for (int i = 0; i < cidadeList.size(); i++) {
                        String[] cidadeVector = new String[4];
                        cidadeVector[0] = String.valueOf(cidadeList.get(i).getId());
                        cidadeVector[1] = cidadeList.get(i).getNome();
                        cidadeVector[2] = String.valueOf(cidadeList.get(i).getSigla());
                        arrayCidade.add(cidadeVector);
                        cidadeName.add(cidadeList.get(i).getNome());
                    }
                    view.formatCidadeAdapter(cidadeName);
                } else {
                    view.showErrorRequest(context.getString(R.string.fail_load_cities));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CidadeRegister> call, @NonNull Throwable t) {
                view.showErrorRequest(context.getString(R.string.fail_load_cities));
            }
        });
    }

    @Override
    public Empresa formatEmpresa(String...data){
        Empresa empresa = new Empresa();
        empresa.setNomeFantasia(data[0]);
        empresa.setCnpj(data[1]);
        empresa.setEstado(data[2]);
        empresa.setCidade(data[3]);
        empresa.setCep(data[4]);
        empresa.setBairro(data[5]);
        empresa.setLogradouro(data[6]);
        empresa.setNumero(data[7]);
        empresa.setComplemento(data[8]);
        return empresa;
    }
}

