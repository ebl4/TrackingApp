package com.example.mapboxapp.Tracking.Model;

public class OfflineNavigationData {
    private String _ID,
    _COLUMN_NAME_CLIENT_NAME,
    _COLUMN_NAME_ORIGEM,
    _COLUMN_NAME_DESTINO,
    _COLUMN_NAME_DISTANCIA,
    _COLUMN_NAME_TEMPO_VIAGEM,
    _COLUMN_NAME_MOTIVO_VIAGEM,
    _COLUMN_NAME_MENOR_ROTA_ENDERECO,
    _COLUMN_NAME_MENOR_ROTA_DISTANCIA,
    _COLUMN_NAME_MENOR_ROTA_TEMPO;

    public OfflineNavigationData(String _ID,
                                 String COLUMN_NAME_CLIENT_NAME,
                                 String COLUMN_NAME_ORIGEM,
                                 String COLUMN_NAME_DESTINO,
                                 String COLUMN_NAME_DISTANCIA,
                                 String COLUMN_NAME_TEMPO_VIAGEM,
                                 String COLUMN_NAME_MOTIVO_VIAGEM,
                                 String COLUMN_NAME_MENOR_ROTA_ENDERECO,
                                 String COLUMN_NAME_MENOR_ROTA_DISTANCIA,
                                 String COLUMN_NAME_MENOR_ROTA_TEMPO){
        this._ID = _ID;
        this._COLUMN_NAME_CLIENT_NAME = COLUMN_NAME_CLIENT_NAME;
        this._COLUMN_NAME_ORIGEM = COLUMN_NAME_ORIGEM;
        this._COLUMN_NAME_DESTINO = COLUMN_NAME_DESTINO;
        this._COLUMN_NAME_DISTANCIA = COLUMN_NAME_DISTANCIA;
        this._COLUMN_NAME_TEMPO_VIAGEM = COLUMN_NAME_TEMPO_VIAGEM;
        this._COLUMN_NAME_MOTIVO_VIAGEM = COLUMN_NAME_MOTIVO_VIAGEM;
        this._COLUMN_NAME_MENOR_ROTA_ENDERECO = COLUMN_NAME_MENOR_ROTA_ENDERECO;
        this._COLUMN_NAME_MENOR_ROTA_DISTANCIA = COLUMN_NAME_MENOR_ROTA_DISTANCIA;
        this._COLUMN_NAME_MENOR_ROTA_TEMPO = COLUMN_NAME_MENOR_ROTA_TEMPO;
    }

    public String get_ID() {
        return _ID;
    }

    public String get_COLUMN_NAME_CLIENT_NAME() {
        return _COLUMN_NAME_CLIENT_NAME;
    }

    public String get_COLUMN_NAME_ORIGEM() {
        return _COLUMN_NAME_ORIGEM;
    }

    public String get_COLUMN_NAME_DESTINO() {
        return _COLUMN_NAME_DESTINO;
    }

    public String get_COLUMN_NAME_DISTANCIA() {
        return _COLUMN_NAME_DISTANCIA;
    }

    public String get_COLUMN_NAME_TEMPO_VIAGEM() {
        return _COLUMN_NAME_TEMPO_VIAGEM;
    }

    public String get_COLUMN_NAME_MOTIVO_VIAGEM() {
        return _COLUMN_NAME_MOTIVO_VIAGEM;
    }

    public String get_COLUMN_NAME_MENOR_ROTA_ENDERECO() {
        return _COLUMN_NAME_MENOR_ROTA_ENDERECO;
    }

    public String get_COLUMN_NAME_MENOR_ROTA_DISTANCIA() {
        return _COLUMN_NAME_MENOR_ROTA_DISTANCIA;
    }

    public String get_COLUMN_NAME_MENOR_ROTA_TEMPO() {
        return _COLUMN_NAME_MENOR_ROTA_TEMPO;
    }
}
