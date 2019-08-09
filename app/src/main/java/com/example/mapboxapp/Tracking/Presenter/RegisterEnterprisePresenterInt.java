package com.example.mapboxapp.Tracking.Presenter;

import com.example.mapboxapp.Tracking.Model.Empresa;

public interface RegisterEnterprisePresenterInt {
    void makeRequest(Empresa empresa);
    void getEstados();
    void setUpCidadeSpinner(int position);
    int returnEstadoCode(int codigo);
    int returnCidadeCode(int codigo);
    Empresa formatEmpresa(String...data);
}
