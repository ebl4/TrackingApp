package com.example.mapboxapp.Tracking.Presenter;
import com.example.mapboxapp.Tracking.Model.Empresa;
import com.google.android.material.textfield.TextInputLayout;

public interface RegisterEnterprisePresenterInt {
    void makeRequest(Empresa empresa);
    boolean checkTextFields(TextInputLayout...fields);
    void getEstados();
    void setUpCidadeSpinner(int position);
    Empresa formatEmpresa(String...data);
}
