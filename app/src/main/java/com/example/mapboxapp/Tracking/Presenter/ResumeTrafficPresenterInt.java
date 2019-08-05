package com.example.mapboxapp.Tracking.Presenter;

import android.widget.TextView;

public interface ResumeTrafficPresenterInt {

    String formatMotivo();

    void formatFields(TextView... views);

    void saveData();
}
