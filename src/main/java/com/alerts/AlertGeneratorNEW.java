package com.alerts;

import com.data_management.Patient;
import java.util.List;

/**
 * AlertGeneratorNEW is a singleton class responsible for evaluating a single patient's data
 * using a list of alert strategies. Each strategy represents a health metric or condition
 * that should be checked for potential alerts.
 *
 * This design allows flexibility and extensibility in determining which strategies
 * to apply during runtime and ensures that alerts are processed and decorated consistently
 * using appropriate factories.
 */
public class AlertGeneratorNEW {

    private static AlertGeneratorNEW instance;

    private final AlertFactory bloodPressureFactory = new BloodPressureAlertFactory();
    private final AlertFactory bloodOxygenFactory = new BloodOxygenAlertFactory();
    private final AlertFactory ecgFactory = new ECGAlertFactory();

    /**
     * Private constructor to enforce the singleton pattern.
     * Use getInstance() to obtain the shared instance.
     */
    private AlertGeneratorNEW() {
        // private constructor for singleton
    }

    /**
     * Returns the singleton instance of AlertGeneratorNEW.
     * If no instance exists, it will be created.
     *
     * @return the shared instance of AlertGeneratorNEW
     */
    public static synchronized AlertGeneratorNEW getInstance() {
        if (instance == null) {
            instance = new AlertGeneratorNEW();
        }
        return instance;
    }

    /**
     * Evaluates a single patient using a list of alert strategies.
     * Each strategy is responsible for detecting a specific type of alert.
     * If an alert is triggered, it is decorated using the appropriate factory
     * and then passed to the trigger mechanism.
     *
     * @param patient the patient whose data will be evaluated
     * @param strategies a list of alert strategies to apply
     */
    public void evaluatePatient(Patient patient, List<AlertStrategy> strategies) {
        for (AlertStrategy strategy : strategies) {
            Alert rawAlert = strategy.checkAlert(patient);
            if (!"none".equals(rawAlert.getCondition())) {
                Alert decoratedAlert = decorateAlert(strategy, rawAlert);
                triggerAlert(decoratedAlert);
            }
        }
    }

    /**
     * Uses the appropriate alert factory to create a more specific decorated alert,
     * based on the type of strategy that triggered it.
     * This helps in categorizing alerts and customizing their behavior or message.
     *
     * @param strategy the strategy that triggered the alert
     * @param baseAlert the original alert returned by the strategy
     * @return a decorated alert with strategy-specific customization
     */
    private Alert decorateAlert(AlertStrategy strategy, Alert baseAlert) {
        if (strategy instanceof BloodPressureStrategy) {
            return bloodPressureFactory.createAlert(baseAlert.getPatientId(), baseAlert.getCondition(), baseAlert.getTimestamp());
        } else if (strategy instanceof OxygenSaturationStrategy) {
            return bloodOxygenFactory.createAlert(baseAlert.getPatientId(), baseAlert.getCondition(), baseAlert.getTimestamp());
        } else if (strategy instanceof HeartRateStrategy || strategy instanceof ECGAlertFactory) {
            return ecgFactory.createAlert(baseAlert.getPatientId(), baseAlert.getCondition(), baseAlert.getTimestamp());
        }
        return baseAlert; // fallback to the original alert if no factory matches
    }

    /**
     * Handles the final alert action such as logging or notifying medical staff.
     * Currently implemented as a console print for demonstration purposes.
     *
     * @param alert the alert that was triggered and decorated
     */
    private void triggerAlert(Alert alert) {
        System.out.println("[ALERT] Patient ID: " + alert.getPatientId()
                + " | Condition: " + alert.getCondition()
                + " | Timestamp: " + alert.getTimestamp());
    }
}
