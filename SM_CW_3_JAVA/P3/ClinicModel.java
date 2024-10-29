package SM_CW_3_JAVA.P3;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.Dispose;
import SM_CW_3_JAVA.P1.simsimple.ITask;
import SM_CW_3_JAVA.P1.simsimple.Model;
import SM_CW_3_JAVA.P1.simsimple.Process;

import java.util.ArrayList;

public class ClinicModel extends Model {
    public ClinicModel(ArrayList<IElement> elements, boolean verbose) {
        super(elements, verbose);
    }

    private double getLaboratoryArrivalInterval() {
        for (var element : getList()) {
            if (element.getName().equals("Laboratory Transfer")) {
                return ((Process) element).getAccumulatedProcessingTime() / element.getQuantity();
            }
        }
        return 0.0;
    }

    @Override
    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        printPatientInfo();
        System.out.println("\n-----------STATISTICS------------");
        System.out.println("Mean time in system (processed): " + getMeanTimeInSystem());
        System.out.println("Mean laboratory arrival interval: " + getLaboratoryArrivalInterval());
    }

    private void printPatientInfo() {
        System.out.println("-------------PATIENTS------------");
        for (var element : getList()) {
            if (element instanceof Dispose d) {
                System.out.println("\n" + element.getName());
                var patients = d.getProcessedJobs();
                for (var patient : patients) {
                    System.out.println("Patient " + patient.getId() +
                            " type " + ((Patient) patient).getType() +
                            " time in " + patient.getTimeIn() +
                            " time out " + patient.getTNext() +
                            " time in system " + (patient.getTNext() - patient.getTimeIn()));
                }
            }
        }
    }

    private double getMeanTimeInSystem() {
        var patients = new ArrayList<ITask>();
        for (var element : getList()) {
            if (element instanceof Dispose d) {
                patients.addAll(d.getProcessedJobs());
            }
        }
        var sum = 0.0;
        for (var patient : patients) {
            sum += patient.getTNext() - patient.getTimeIn();
        }
        return sum / patients.size();
    }
}
