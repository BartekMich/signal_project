package data_management;

import com.alerts.*;
import com.data_management.Patient;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertGeneratorNEWTest {

    static class DummyAlertStrategy implements AlertStrategy {
        @Override
        public Alert checkAlert(Patient patient) {
            return new Alert("999", "Simulated Condition", 999L);
        }
    }

    static class NoOpAlertStrategy implements AlertStrategy {
        @Override
        public Alert checkAlert(Patient patient) {
            return new Alert("999", "none", System.currentTimeMillis());
        }
    }

    @Test
    void testEvaluatePatientWithTriggeredAlert() {
        Patient patient = new Patient(1);
        List<AlertStrategy> strategies = new ArrayList<>();
        strategies.add(new DummyAlertStrategy());

        AlertGeneratorNEW generator = AlertGeneratorNEW.getInstance();
        generator.evaluatePatient(patient, strategies);

        // Output is printed to console — check manually or redirect output for assert
    }

    @Test
    void testEvaluatePatientWithNoAlert() {
        Patient patient = new Patient(2);
        List<AlertStrategy> strategies = new ArrayList<>();
        strategies.add(new NoOpAlertStrategy());

        AlertGeneratorNEW generator = AlertGeneratorNEW.getInstance();
        generator.evaluatePatient(patient, strategies);

        // No output expected; test passes if no exceptions occur
    }

    @Test
    void testSingletonInstanceNotNull() {
        AlertGeneratorNEW generator1 = AlertGeneratorNEW.getInstance();
        AlertGeneratorNEW generator2 = AlertGeneratorNEW.getInstance();
        assertSame(generator1, generator2, "Singleton instances should be the same");
    }
}
