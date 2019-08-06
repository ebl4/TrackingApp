package com.example.mapboxapp.Tracking.View;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Presenter.ResumeTrafficPresenter;
import com.example.mapboxapp.Tracking.Presenter.ResumeTrafficPresenterInt;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResumeTrafficActivity extends AppCompatActivity {

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
    @BindView(R.id.btnFinalizar)
    Button btnFinalizar;
    PreferenceConfig prefConfig;
    ResumeTrafficPresenterInt presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_traffic);
        ButterKnife.bind(this);
        prefConfig = new PreferenceConfig(this);
        presenter = new ResumeTrafficPresenter(this);
        presenter.formatFields(txtDistance, txtTime, txtPartida, txtChegada, txtStatus);

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), VisitActivity.class);
                startActivity(i);
                presenter.saveData();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), VisitActivity.class);
        startActivity(i);
        finish();
    }
}
