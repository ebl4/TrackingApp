package com.example.mapboxapp.Tracking.Presenter;

import java.util.HashMap;
import java.util.List;

public interface VisitPresenterInt {
    /**
     * Check if exists offline data
     * for tracking in the local database
     * @return true or false if has tracking offline data
     */
    boolean checkOfflineData();

    /**
     * Retrieves offline data of the tracking
     * and set them in a collection
     */
    void retriveOfflineData();

    /**
     * Save navigation offline data if them
     * exist in the local database
     */
    void saveData();
    void retriveCustomerOfflineData(HashMap<String, String> clients, List<String> clientNames);
    void updateOfflineCustomerData(HashMap<String, String> clients, List<String> clientNames, boolean synchronize);
    boolean checkCustomerOfflineData();
}
