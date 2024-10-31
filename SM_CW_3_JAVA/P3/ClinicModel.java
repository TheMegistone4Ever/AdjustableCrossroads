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
        for (IElement element : getList()) {
            if (element.getName().equals("Laboratory Transfer")) {
                return ((Process) element).getAccumulatedProcessingTime() / element.getQuantity();
            }
        }
        return 0.0;
    }

    @Override
    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        super.printResult();
        printSickInfo();
        System.out.println("\n-----------STATISTICS------------");
        System.out.println("Mean time in system (processed): " + getMeanTimeInSystem());
        System.out.println("Mean laboratory arrival interval: " + getLaboratoryArrivalInterval());
    }

    private void printSickInfo() {
        System.out.println("-------------SICKS------------");
        for (IElement element : getList()) {
            if (element instanceof Dispose d) {
                System.out.println("\n" + element.getName());
                for (ITask sick : d.getProcessedJobs()) {
                    if (sick == null) {
                        continue;
                    }
                    System.out.println("Sick " + sick.getId() +
                            " type " + ((Sick) sick).getType() +
                            " time in " + sick.getTimeIn() +
                            " time out " + sick.getTNext() +
                            String.format("time in %s %.6f", ((Sick) sick).getType() == 3 ? "system" : "ward",
                                    sick.getTNext() - sick.getTimeIn()));
                }
            }
        }
    }

    private double getMeanTimeInSystem() {
        ArrayList<ITask> sicks = new ArrayList<>();
        for (IElement element : getList()) {
            if (element instanceof Dispose d) {
                sicks.addAll(d.getProcessedJobs());
            }
        }
        double sum = 0.0;
        int size = 0;
        for (ITask sick : sicks) {
            if (sick == null) {
                continue;
            }
            sum += sick.getTNext() - sick.getTimeIn();
            ++size;
        }
        return sum / size;
    }
}
