package com.alerts;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.alerts.Alert;

public interface AlertTester {

    Alert check(Patient patient);

    
}
