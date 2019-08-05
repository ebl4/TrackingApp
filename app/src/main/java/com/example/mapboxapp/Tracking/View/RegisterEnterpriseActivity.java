package com.example.mapboxapp.Tracking.View;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mapboxapp.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_enterprise);
        ButterKnife.bind(this);

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
}
