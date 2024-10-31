package SM_CW_3_JAVA.P3;

import SM_CW_2_JAVA.P1.simsimple.Distribution;
import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.Process;
import SM_CW_3_JAVA.P1.simsimple.*;

import java.util.ArrayList;

public class SimModel {
    public static void main(String[] args) {
        final int[] sickTypes = {1, 2, 3};
        final double[] sickFrequencies = {0.5, 0.1, 0.4};
        final double[] sickDelays = {15, 40, 30};

        CreateSick create = new CreateSick("Sick Creator", 15);
        FormProcess form = new FormProcess("Form", 15, 2);
        Process wardsTransfer = new Process("Wards Transfer", 3, 8, -1,3);
        Process laboratoryTransfer = new Process("Laboratory Transfer", 2, 5, -1, 100);
        Process laboratoryForm = new Process("Laboratory Form", 4.5, 3, -1, 1);
        TypeModifyingProcess laboratoryAnalysis = new TypeModifyingProcess("Laboratory Analysis", 4, 2, 2);
        Process FormTransfer = new Process("Form Transfer", 2, 5, 100);

        Dispose wardsDispose = new Dispose("Dispose 1,2");
        Dispose laboratoryDispose = new Dispose("Dispose 3");


        create.setSickTypedFrequencies(sickTypes, sickFrequencies);
        form.setSickTypedDelays(sickTypes, sickDelays);
        form.setPrioritizedSickType(1);
        laboratoryAnalysis.setTypeModifyingMap(
                new int[]{2},
                new int[]{1}
        );

        wardsTransfer.setDistribution(Distribution.UNIFORM);
        laboratoryTransfer.setDistribution(Distribution.UNIFORM);
        laboratoryForm.setDistribution(Distribution.ERLANG);
        laboratoryAnalysis.setDistribution(Distribution.ERLANG);
        FormTransfer.setDistribution(Distribution.UNIFORM);

        create.addPaths(
                new Path(form)
        );
        form.addPaths(
                new Path(wardsTransfer, 0.5, 2, (ITask task) -> (
                        (Sick) task).getType() != 1
                        || wardsTransfer.getQueue().size() == wardsTransfer.getMaxQueue()
                ),
                new Path(laboratoryTransfer, 0.5, 1)
        );
        form.setForking(Forking.PRIORITIZED);
        wardsTransfer.addPaths(
                new Path(wardsDispose)
        );
        laboratoryTransfer.addPaths(
                new Path(laboratoryForm)
        );
        laboratoryForm.addPaths(
                new Path(laboratoryAnalysis)
        );
        laboratoryAnalysis.addPaths(
                new Path(laboratoryDispose, 2, (ITask task) -> ((Sick) task).getType() != 3),
                new Path(FormTransfer, 1)
        );
        laboratoryAnalysis.setForking(Forking.PRIORITIZED);
        FormTransfer.addPaths(
                new Path(form)
        );

        ArrayList<IElement> elements = new ArrayList<>();
        elements.add(create);
        elements.add(form);
        elements.add(wardsTransfer);
        elements.add(laboratoryTransfer);
        elements.add(laboratoryForm);
        elements.add(laboratoryAnalysis);
        elements.add(FormTransfer);
        elements.add(wardsDispose);
        elements.add(laboratoryDispose);
        ClinicModel model = new ClinicModel(elements, true);
        model.simulate(1000);
        model.printResult();
    }
}
