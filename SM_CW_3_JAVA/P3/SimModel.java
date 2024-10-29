package SM_CW_3_JAVA.P3;

import SM_CW_2_JAVA.P1.simsimple.Distribution;
import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.Process;
import SM_CW_3_JAVA.P1.simsimple.*;

import java.util.ArrayList;

public class SimModel {
    public static void main(String[] args) {
        final int[] patientTypes = {1, 2, 3};
        final double[] patientFrequencies = {0.5, 0.1, 0.4};
        final double[] patientDelays = {15, 40, 30};

        var create = new PatientCreate("Patient Creator", 15);
        var registration = new RegistrationProcess("Registration", 15, 2);
        var wardsTransfer = new SM_CW_3_JAVA.P1.simsimple.Process("Wards Transfer", 3, 8, 3);
        var laboratoryTransfer = new SM_CW_3_JAVA.P1.simsimple.Process("Laboratory Transfer", 2, 5, 100);
        var laboratoryRegistration = new SM_CW_3_JAVA.P1.simsimple.Process("Laboratory Registration", 4.5, 3, 1);
        var laboratoryAnalysis = new TypeModifyingProcess("Laboratory Analysis", 4, 2, 2);
        var registrationTransfer = new Process("Registration Transfer", 2, 5, 100);

        var wardsDispose = new Dispose("Dispose [Type 1 & 2]");
        var laboratoryDispose = new Dispose("Dispose [Type 3]");


        create.setPatientTypedFrequencies(patientTypes, patientFrequencies);
        registration.setPatientTypedDelays(patientTypes, patientDelays);
        registration.setPrioritizedPatientType(1);
        laboratoryAnalysis.setTypeModifyingMap(
                new int[]{2},
                new int[]{1}
        );

        create.setDistribution(Distribution.EXPONENTIAL);
        registration.setDistribution(Distribution.EXPONENTIAL);
        wardsTransfer.setDistribution(Distribution.UNIFORM);
        laboratoryTransfer.setDistribution(Distribution.UNIFORM);
        laboratoryRegistration.setDistribution(Distribution.ERLANG);
        laboratoryAnalysis.setDistribution(Distribution.ERLANG);
        registrationTransfer.setDistribution(Distribution.UNIFORM);

        create.addPaths(
                new Path(registration)
        );
        registration.addPaths(
                new Path(wardsTransfer, 0.5, 1, (ITask task) -> ((Patient) task).getType() != 1),
                new Path(laboratoryTransfer, 0.5, 0)
        );
        registration.setForking(Forking.PRIORITIZED);
        wardsTransfer.addPaths(
                new Path(wardsDispose)
        );
        laboratoryTransfer.addPaths(
                new Path(laboratoryRegistration)
        );
        laboratoryRegistration.addPaths(
                new Path(laboratoryAnalysis)
        );
        laboratoryAnalysis.addPaths(
                new Path(laboratoryDispose, 0.5, 1, (ITask task) -> ((Patient) task).getType() != 3),
                new Path(registrationTransfer, 0.5, 0)
        );
        laboratoryAnalysis.setForking(Forking.PRIORITIZED);
        registrationTransfer.addPaths(
                new Path(registration)
        );

        ArrayList<IElement> elements = new ArrayList<>();
        elements.add(create);
        elements.add(registration);
        elements.add(wardsTransfer);
        elements.add(laboratoryTransfer);
        elements.add(laboratoryRegistration);
        elements.add(laboratoryAnalysis);
        elements.add(registrationTransfer);
        elements.add(wardsDispose);
        elements.add(laboratoryDispose);
        var model = new ClinicModel(elements, true);
        model.simulate(1000);
        model.printResult();
    }
}
