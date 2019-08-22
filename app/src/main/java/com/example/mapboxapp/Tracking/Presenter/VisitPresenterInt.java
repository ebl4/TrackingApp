package com.example.mapboxapp.Tracking.Presenter;

import com.example.mapboxapp.Tracking.Database.TrackingDbHelper;

public interface VisitPresenterInt {
    boolean checkOfflineData();
    void retriveOfflineData();
    void saveData();
}
