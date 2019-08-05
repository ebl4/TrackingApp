package com.example.mapboxapp.Tracking.View;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mapboxapp.R;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfig;
import com.example.mapboxapp.Tracking.Utils.PreferenceConfigInt;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CancelNavigationActivity extends AppCompatActivity {

    @BindView(R.id.edtMotivo)
    EditText edtMotivo;
    @BindView(R.id.btnCancelYes)
    Button cancelNavigationButton;
    @BindView(R.id.btnCancelNo)
    Button cancelNavigationNoButton;
    PreferenceConfigInt prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_navigation);
        ButterKnife.bind(this);
        prefs = new PreferenceConfig(this);
        cancelNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResumeTrafficActivity.class);
                prefs.putString(getString(R.string.motivoCancelamento), edtMotivo.getText().toString());
                startActivity(intent);
                finishAffinity();
            }
        });
        cancelNavigationNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
