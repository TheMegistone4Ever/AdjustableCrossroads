package SM_CW_3_JAVA.P3;

import SM_CW_2_JAVA.P1.simsimple.Distribution;
import SM_CW_3_JAVA.P1.simsimple.Process;
import SM_CW_3_JAVA.P1.simsimple.*;

import java.util.ArrayList;
import java.util.List;

public class SimModel {
    public static void main(String[] args) {
        final int[] sickTypes = {1, 2, 3};
        final double[] sickFrequencies = {.5, .1, .4};
        final double[] sickDelays = {15, 40, 30};

        final CreateSick create = new CreateSick("Sick Creator", 15);
        final FormProcess form = new FormProcess("Form", 15, 2);
        final Process wardsTransfer = new Process("Wards Transfer", 3, 8, -1, 3);
        final Process labTransfer = new Process("Laboratory Transfer", 2, 5, -1, 100);
        final Process labForm = new Process("Laboratory Form", 4.5, 3, -1, 1);
        final TypeModifyingProcess labAnalysis = new TypeModifyingProcess("Laboratory Analysis", 4, 2, 2);
        final Process formTransfer = new Process("Form Transfer", 2, 5, 100);
        final Dispose wardsDispose = new Dispose("Dispose 1,2");
        final Dispose labDispose = new Dispose("Dispose 3");

        create.setSickTypedFrequencies(sickTypes, sickFrequencies);
        form.setSickTypedDelays(sickTypes, sickDelays);
        form.setPrioritizedSickType(1);
        labAnalysis.setTypeModifyingMap(new int[]{2}, new int[]{1});

        wardsTransfer.setDistribution(Distribution.UNIFORM);
        labTransfer.setDistribution(Distribution.UNIFORM);
        labForm.setDistribution(Distribution.ERLANG);
        labAnalysis.setDistribution(Distribution.ERLANG);
        formTransfer.setDistribution(Distribution.UNIFORM);

        create.addPaths(new Path(form));
        form.addPaths(new Path(wardsTransfer, .5, 2, (ITask task) -> ((Sick) task).getType() != 1
                || wardsTransfer.getQueue().size() == wardsTransfer.getMaxQueue()), new Path(labTransfer, .5, 1));
        form.setForking(Forking.PRIORITIZED);
        wardsTransfer.addPaths(new Path(wardsDispose));
        labTransfer.addPaths(new Path(labForm));
        labForm.addPaths(new Path(labAnalysis));
        labAnalysis.addPaths(new Path(labDispose, 2, (ITask task) -> ((Sick) task).getType() != 3),
                new Path(formTransfer, 1));
        labAnalysis.setForking(Forking.PRIORITIZED);
        formTransfer.addPaths(new Path(form));

        final ClinicModel model = new ClinicModel(new ArrayList<>(List.of(create, form, wardsTransfer, labTransfer,
                labForm, labAnalysis, formTransfer, wardsDispose, labDispose)), true);
        model.simulate(1000);
        model.printResult();
    }
}
