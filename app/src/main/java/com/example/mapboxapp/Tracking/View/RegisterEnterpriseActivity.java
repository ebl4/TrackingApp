package com.example.mapboxapp.Tracking.View;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Model.Address;
import com.example.mapboxapp.Tracking.Model.Empresa;
import com.example.mapboxapp.Tracking.Presenter.RegisterEnterprisePresenter;
import com.example.mapboxapp.Tracking.Presenter.RegisterEnterprisePresenterInt;
import com.example.mapboxapp.Tracking.Utils.Util;
import com.example.mapboxapp.Tracking.Utils.ZipCodeListener;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class RegisterEnterpriseActivity extends AppCompatActivity {

    @BindView(R.id.inputName)
    TextInputLayout inputName;
    @BindView(R.id.inputLogradouro)
    TextInputLayout inputLogradouro;
    @BindView(R.id.inputNumero)
    TextInputLayout inputNumero;
    @BindView(R.id.inputBairro)
    TextInputLayout inputBairro;
    @BindView(R.id.inputCEP)
    TextInputLayout inputCEP;
    @BindView(R.id.inputCidade)
    Spinner inputCidade;
    @BindView(R.id.inputEstado)
    Spinner inputEstado;
    @BindView(R.id.btnCadastrar)
    Button btnCadastrar;
    private Util util;
    RegisterEnterprisePresenterInt presenter;

    public static final String VIACEP_URL = "https://viacep.com.br/ws/";
    public static final String RESOURCES_URL = "https://ssm.sette.inf.br/SSM_GBS/RecusosServices/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_enterprise);
        ButterKnife.bind(this);
        presenter = new RegisterEnterprisePresenter(this, this);
        presenter.getEstados();

        inputEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.setUpCidadeSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        util = new Util(this, R.id.inputLogradouro, R.id.inputBairro);
        inputCEP.getEditText().addTextChangedListener(new ZipCodeListener(this));
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    public void saveData(){
        String edtNome, edtLogradouro, edtNumero, edtBairro, edtCEP, edtEstado, edtCidade;
        Empresa empresa = new Empresa();
        edtNome = inputName.getEditText().getText().toString();
        int codCidade, codEstado;
        edtLogradouro = inputLogradouro.getEditText().getText().toString();
        edtNumero = inputNumero.getEditText().getText().toString();
        edtBairro = inputBairro.getEditText().getText().toString();
        edtCEP = inputCEP.getEditText().getText().toString();
        edtCidade = inputCidade.getSelectedItem().toString();
        edtEstado = inputEstado.getSelectedItem().toString();
        codEstado = presenter.returnEstadoCode(inputEstado.getSelectedItemPosition());

        if(edtNome.length() <= 3){
            inputName.setError("Por favor digite um nome válido");
            inputName.requestFocus();
        }
        else if(edtLogradouro.length() <= 3){
            inputLogradouro.setError("Por favor digite um logradouro válido");
            inputLogradouro.requestFocus();
        }
        else if(edtNumero.length() <= 0){
            inputNumero.setError("Por favor digite um número válido");
            inputNumero.requestFocus();
        }
        else if(edtBairro.length() <= 3){
            inputBairro.setError("Por favor digite um bairro válido");
            inputBairro.requestFocus();
        }
        else if(edtCEP.length() <= 3){
            inputCEP.setError("Por favor digite um cep válido");
            inputCEP.requestFocus();
        }
        empresa = presenter.formatEmpresa(edtNome, "", edtEstado, edtCidade, edtCEP,
                edtBairro, edtLogradouro, edtNumero, "");
        presenter.makeRequest(empresa);
        Intent intent = new Intent(this, VisitActivity.class);
        intent.putExtra("cliente", edtNome);
        intent.putExtra("endereco", edtLogradouro + " "
                + edtNumero + " " + edtBairro + " " + edtCidade);
        startActivity(intent);
        finish();
        //make request
    }

    private String getZipCode(){
        return inputCEP.getEditText().getText().toString();
    }

    public String getUriRequest(){
        return VIACEP_URL + getZipCode() + "/json/";
    }

    public void lockFields( boolean isToLock ){
        util.lockFields( isToLock );
    }

    public void setAddressFields( Address address){
        setField( R.id.inputLogradouro, address.getLogradouro() );
        setField( R.id.inputBairro, address.getBairro() );
    }

    private void setField( int fieldId, String data ){
        ((TextInputLayout) findViewById( fieldId )).getEditText().setText( data );
    }

    public void formatEstadoAdapter(List<String> estadoSigla) {
        ArrayAdapter<String> spnEstadoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estadoSigla);
        inputEstado.setAdapter(spnEstadoAdapter);
        spnEstadoAdapter.notifyDataSetChanged();
    }

    public void formatCidadeAdapter(List<String> cidadeName) {
        ArrayAdapter<String> spnCidadeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cidadeName);
        inputCidade.setAdapter(spnCidadeAdapter);
        spnCidadeAdapter.notifyDataSetChanged();
    }

    public void showErrorRequest(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }
}
