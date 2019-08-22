package com.example.mapboxapp.Tracking.Model;

import android.widget.TextView;

public interface ResumeTrafficPresenterInt {

    String formatMotivo();

    void formatFields(TextView... views);

    void saveData();
}
