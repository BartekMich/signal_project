package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The {@code BloodSaturationDataAlerts} class detects critical conditions related to blood oxygen saturation.
 *
 * This class implements the {@link AlertTester} interface and evaluates two primary alert conditions:
 * 
 *     Rapid Drop Alert: Triggers if blood oxygen saturation drops by ≥ 5% within a 10-minute window.
 *     Low Saturation Alert:Triggers if saturation falls below 92% at any point (if no rapid drop detected).
 * 
 *
 * Assumptions:
 * 
 *     All records with type \"BloodOxygenSaturation\" are considered (case-insensitive match).
 *     Records are timestamped in milliseconds and sorted before analysis.
 *     Only one alert is returned per evaluation: the most urgent (rapid drop is prioritized).
 * 
 */
public class BloodSaturationDataAlerts implements AlertTester {

    /**
     * Checks for oxygen saturation alerts on the given patient.
     * The method prioritizes rapid drops over low saturation to highlight more urgent trends.
     *
     * @param patient the patient to evaluate
     * @return an {@link Alert} describing the condition, or \"none\" if all values are within safe range
     */
    @Override
    public Alert check(Patient patient) {
        List<PatientRecord> allRecords = patient.getRecords(0, Long.MAX_VALUE);
        List<PatientRecord> saturationRecords = new ArrayList<>();

        // Manually filter for BloodOxygenSaturation records
        for (PatientRecord record : allRecords) {
            if (record.getRecordType().equalsIgnoreCase("BloodOxygenSaturation")) {
                saturationRecords.add(record);
            }
        }

        saturationRecords.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        // Rapid Drop Alert: Check if saturation drops by ≥ 5% within 10 minutes (600,000 ms)
        for (int i = 0; i < saturationRecords.size(); i++) {
            PatientRecord earlier = saturationRecords.get(i);
            for (int j = i + 1; j < saturationRecords.size(); j++) {
                PatientRecord later = saturationRecords.get(j);
                long timeDiff = later.getTimestamp() - earlier.getTimestamp();
                if (timeDiff > 600_000) break;

                double drop = earlier.getMeasurementValue() - later.getMeasurementValue();
                if (drop >= 5.0) {
                    return new Alert(
                            String.valueOf(patient.getPatientId()),
                            "Rapid Drop in Blood Oxygen Saturation: from " +
                                    earlier.getMeasurementValue() + " to " + later.getMeasurementValue(),
                            later.getTimestamp()
                    );
                }
            }
        }

        // Low Saturation Alert: If no rapid drop found, check if any value < 92%
        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92.0) {
                return new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Low Blood Oxygen Saturation: " + record.getMeasurementValue(),
                        record.getTimestamp()
                );
            }
        }

        return new Alert(String.valueOf(patient.getPatientId()), "none", System.currentTimeMillis());
    }
}
