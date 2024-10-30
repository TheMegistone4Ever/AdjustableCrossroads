package SM_CW_3_JAVA.P3;

import SM_CW_3_JAVA.P1.simsimple.Create;
import SM_CW_3_JAVA.P1.simsimple.ITask;

import java.util.HashMap;

public class PatientCreate extends Create {

    private HashMap<Integer, Double> patientTypedFrequencies;

    public PatientCreate(String name, double delay) {
        super(name, delay);
    }

    public void setPatientTypedFrequencies(int[] types, double[] frequencies) {
        this.patientTypedFrequencies = new HashMap<>();
        for (int i = 0; i < types.length; ++i) {
            this.patientTypedFrequencies.put(types[i], frequencies[i]);
        }
    }

    @Override
    protected ITask createTask() {
        return new Patient(super.getTCurr(), choosePatientType());
    }

    private int choosePatientType() {
        double random = Math.random();
        double sum = 0;
        for (int type : patientTypedFrequencies.keySet()) {
            sum += patientTypedFrequencies.get(type);
            if (random <= sum) {
                return type;
            }
        }
        return -1;
    }
}
