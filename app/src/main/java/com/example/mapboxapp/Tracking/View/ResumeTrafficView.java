package com.example.mapboxapp.Tracking.View;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Presenter.ResumeTrafficPresenter;
import com.example.mapboxapp.Tracking.Presenter.ResumeTrafficPresenterInt;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.View.Fragments.VisitFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
    Resume View responsible for display tracking information
    and save data in the Web Service
 */
public class ResumeTrafficView extends AppCompatActivity implements ResumeTrafficViewInt{

    @BindView(R.id.txtDistance)
    TextView txtDistance;
    @BindView(R.id.txtResumeTime)
    TextView txtTime;
    @BindView(R.id.txtPartida)
    TextView txtPartida;
    @BindView(R.id.txtChegada)
    TextView txtChegada;
    @BindView(R.id.txtStatus)
    TextView txtStatus;
    @BindView(R.id.ResumeView)
    View view;
    @BindView(R.id.btnFinalizar)
    Button btnFinalizar;
    PreferenceConfig prefConfig;
    ProgressDialog mProgressDialog;


    ResumeTrafficPresenterInt presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_traffic);
        ButterKnife.bind(this);
        prefConfig = new PreferenceConfig(this);
        presenter = new ResumeTrafficPresenter(this, this);
        presenter.formatFields(txtDistance, txtTime, txtPartida, txtChegada, txtStatus);
        mProgressDialog = new ProgressDialog(this);

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.saveData();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //presenter.saveData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //presenter.saveData();
        finish();
    }

    @Override
    public void showNavigationSuccess(String navigationSuccess) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(navigationSuccess);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                (dialogInterface, in) -> {
                    Intent i = new Intent(getApplicationContext(), HomeView.class);
                    startActivity(i);
                    finish();
                });
        alertDialog.show();
        //Toast.makeText(this, navigationSuccess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog() {

        if(mProgressDialog != null && !mProgressDialog.isShowing()){
            mProgressDialog.setMessage("Carregando...");
            mProgressDialog.show();
        }
    }

    @Override
    public void hideDialog() {

        if(mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}