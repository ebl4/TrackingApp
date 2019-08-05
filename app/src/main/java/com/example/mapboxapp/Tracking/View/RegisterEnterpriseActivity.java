package com.example.mapboxapp.Tracking.View;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Model.Address;
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
    TextInputLayout inputCidade;
    @BindView(R.id.inputEstado)
    TextInputLayout inputEstado;
    @BindView(R.id.btnCadastrar)
    Button btnCadastrar;
    private Util util;

    public static final String VIACEP_URL = "https://viacep.com.br/ws/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_enterprise);
        ButterKnife.bind(this);
        util = new Util(this, R.id.inputCidade, R.id.inputLogradouro, R.id.inputBairro, R.id.inputEstado);
        inputCEP.getEditText().addTextChangedListener(new ZipCodeListener(this));
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    public void saveData(){
        String edtNome, edtLogradouro, edtNumero, edtBairro, edtCEP, edtCidade, edtEstado;
        edtNome = inputName.getEditText().getText().toString();
        edtLogradouro = inputLogradouro.getEditText().getText().toString();
        edtNumero = inputNumero.getEditText().getText().toString();
        edtBairro = inputBairro.getEditText().getText().toString();
        edtCEP = inputCEP.getEditText().getText().toString();
        edtCidade = inputCidade.getEditText().getText().toString();
        edtEstado = inputEstado.getEditText().getText().toString();

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
        else if(edtEstado.length() < 2){
            inputEstado.setError("Por favor digite um Estado válido");
            inputEstado.requestFocus();
        }
        else if(edtCidade.length() <= 3){
            inputCidade.setError("Por favor digite um cidade válida");
            inputCidade.requestFocus();
        }
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
        setField(R.id.inputCidade, address.getLocalidade());
        setField(R.id.inputEstado, address.getUf());
    }

    private void setField( int fieldId, String data ){
        ((TextInputLayout) findViewById( fieldId )).getEditText().setText( data );
    }
}
