package com.example.mapboxapp.Tracking.View;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import androidx.annotation.Nullable;

import com.example.mapboxapp.Tracking.Database.TrackingDbHelper;
import com.example.mapboxapp.Tracking.Presenter.VisitPresenter;
import com.example.mapboxapp.Tracking.Presenter.VisitPresenterInt;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfigInt;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Retrofit.RetrofitConfig;
import com.example.mapboxapp.Retrofit.services.Services;
import com.example.mapboxapp.Tracking.Model.Dado;
import com.example.mapboxapp.Tracking.Model.GPSCoordinates;
import com.example.mapboxapp.Tracking.Model.UserRegister;
import com.example.mapboxapp.Tracking.Utils.PreferencesManager;
import com.example.mapboxapp.Tracking.Utils.PreferencesManagerInt;
import com.example.mapboxapp.Tracking.Utils.SingleShotLocationProvider;
import com.mapbox.geojson.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitView extends AppCompatActivity implements VisitViewInt {
    private static final String TAG = "VisitView";
    public static final String CLIENTS_URL = "https://ssm.sette.inf.br/SSM_GBS/SsmServices/";
    private static final int REQUEST_LOCATION = 1;

    @BindView(R.id.edtPartida)
    TextInputLayout edtPartida;
    @BindView(R.id.edtEndereco)
    TextInputLayout edtDestinoAddress;
    @BindView(R.id.edtMotivo)
    TextInputLayout edtMotivo;
    @BindView(R.id.edtDestino)
    AutoCompleteTextView edtDestinoName;
    @BindView(R.id.btnRoute)
    Button btnRoute;
    @BindView(R.id.fabNewClient)
    FloatingActionButton fabNewClient;
    @BindView(R.id.VisitView)
    View view;

    private TrackingDbHelper dbHelper;
    private VisitPresenterInt presenter;
    private PreferencesManagerInt prefs;
    private PreferenceConfigInt prefConfig;
    private boolean gpsGranted;

    ArrayAdapter<String> adapter;
    HashMap<String, String> clients;
    ArrayList<String> clientNames;

    public static GPSCoordinates gpsCoordinates;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);
        Bundle bundle = getIntent().getExtras();
        ButterKnife.bind(this);
        prefs = new PreferencesManager(this);
        prefConfig = new PreferenceConfig(this);
        dbHelper = new TrackingDbHelper(this);
        presenter = new VisitPresenter(this, this);

        if(bundle != null){
            edtDestinoName.setText(bundle.get("cliente").toString());
            edtDestinoAddress.getEditText().setText(bundle.get("endereco").toString());
        }

        checkOfflineData();
        requestPermission();
        gpsGranted = prefs.getBoolean(getString(R.string.gpsGranted), 1);
        clientNames = new ArrayList<String>();
        getClients();
        if (gpsGranted) {
            SingleShotLocationProvider.requestSingleUpdate(this, this::setLocation);
            btnRoute.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    performRoute();
                }
            });

        } else {
            // GPS nao ativado
            fineLocationExplanation();
        }
        fabNewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new client address
                Intent intent = new Intent(getApplicationContext(), RegisterEnterpriseActivity.class);
                startActivity(intent);
            }
        });

        edtDestinoName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edtDestinoAddress.getEditText().setText(clients.get(edtDestinoName.getText().toString()));
            }
        });
    }

    public void formatMotivo(){
        prefs.putString(getString(R.string.motivoViagem), edtMotivo.getEditText().getText().toString(), 1);
    }

    public void formatNomeDestino(){
        prefConfig.putString(getString(R.string.clientName), edtDestinoName.getText().toString());
    }

    public void clearPreferences(){
        prefs.clearPreferences(1);
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            prefs.putBoolean(getString(R.string.gpsGranted), true, 1);
        }
    }

    public void checkOfflineData(){
        if(presenter.checkOfflineData()){
            offlineDataAlert();
        }
    }

    public void getClients(){
        Services service = RetrofitConfig.getRetrofitInstance(CLIENTS_URL).create(Services.class);
        Call<UserRegister> call = service.getEmpresas();
        clients = new HashMap<>();

        call.enqueue(new Callback<UserRegister>() {
            @Override
            public void onResponse(Call<UserRegister> call, Response<UserRegister> response) {
                if(response.body() != null){
                    for (Dado user: response.body().getDados()) {
                        StringBuilder clientAddress = new StringBuilder();
                        user.setLogradouro(formatAddress(user.getLogradouro()));
                        clientAddress.append(user.getLogradouro().trim()).append(" ")
                                .append(user.getNumero().trim()).append(" ")
                                .append(user.getBairro().trim()).append(" ")
                                .append(user.getCidade());
                        clientNames.add(user.getNomeFantasia());
                        clients.put(user.getNomeFantasia(), clientAddress.toString());
                    }
                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, clientNames);
                    edtDestinoName.setAdapter(adapter);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Não foi possível carregar os endereços", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<UserRegister> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Não foi possível carregar os endereços", Toast.LENGTH_SHORT);
            }
        });
    }

    public String formatAddress(String logradouro){
        if (logradouro.substring(0, 2).equalsIgnoreCase("R."))
            logradouro = logradouro.replace("R.", "Rua");
        return logradouro;
    }

    public void setLocation(GPSCoordinates location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String address, rua, cidade, numero;
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 2);
            address = addresses.get(0).getAddressLine(0);

            rua = address.split(",")[0];
            cidade = address.split(",")[2].split("-")[0];
            numero = addresses.get(0).getFeatureName();

            if(!rua.isEmpty() && !cidade.isEmpty() && !numero.isEmpty()) {
                String partida = rua + ", " + numero + ". " + cidade;
                Objects.requireNonNull(edtPartida.getEditText()).setText(partida);
                location.partida = partida;
                gpsCoordinates = location;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setDestLocation() {
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(gpsCoordinates.destino,5);
            if (address == null || address.isEmpty()) {
                return(false);
            }
            Address location = address.get(0);
            gpsCoordinates.destPoint = Point.fromLngLat(location.getLongitude(), location.getLatitude());
        } catch (IOException e) {
            Toast.makeText(this, "Não foi possível iniciar a navegação. Verifique a conexão com a internet ou" +
                    "tente novamente mais tarde", Toast.LENGTH_SHORT).show();
            return false;
        }

        return(true);
    }

    public void performRoute() {
        if(!edtDestinoAddress.getEditText().getText().toString().isEmpty()) {
            gpsCoordinates.destino = edtDestinoAddress.getEditText().getText().toString();
            if(setDestLocation()) {
                intent = new Intent(this, RoutesActivity.class);
                formatNomeDestino();
                formatMotivo();
                startActivity(intent);
            }
        } else {
            edtDestinoAddress.getEditText().setError("Você deve preencher o destino.");
            edtDestinoAddress.getEditText().requestFocus();
        }
    }

    public void navigationOptionsAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getString(R.string.wazeNavigation));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                (dialogInterface, in) -> {
                    // If the navigation view didn't need to do anything, call super
                    try
                    {
                        String destination = gpsCoordinates.destino.replace(" ", "%20");
                        String url = "https://waze.com/ul?q=" + destination +
                                "&ll=" + gpsCoordinates.latitude + "," + gpsCoordinates.longitude +
                                "&navigate=yes";
                        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                        startActivity( intent );
                    }
                    catch ( ActivityNotFoundException ex  )
                    {
                        // If Waze is not installed, open it in Google Play:
                        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                        startActivity(intent);
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.defaultNavigation),
                (dialogInterface, in) -> {
                    intent = new Intent(this, RoutesActivity.class);
                    formatNomeDestino();
                    formatMotivo();
                    startActivity(intent);
                });
        alertDialog.show();
    }

    public void offlineDataAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getString(R.string.alertSaveData));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                (dialogInterface, in) -> {
                    // If the navigation view didn't need to do anything, call super
                    presenter.saveData();
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.sincronizationAfter),
                (dialogInterface, in) -> {
                    // Do nothing
                });
        alertDialog.show();
    }

    public void fineLocationExplanation() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Localização")
                .setMessage("A permissão de localização deve estar ativada" +
                        " para utilizar essa funcionalidade.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void showNavigationSuccess(String navigationSuccess){
        Snackbar.make(view, navigationSuccess, Snackbar.LENGTH_LONG).show();
    }
}
