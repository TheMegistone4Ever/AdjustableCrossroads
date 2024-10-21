package SM_CW_2_JAVA.P4;

import SM_CW_2_JAVA.P2.simsimple.Model;

import static SM_CW_2_JAVA.P3.SimModel.createModel;

public class SimModels {
    public static void main(String[] args) {
        double[] createTimes = {.5, 1., 2., 3., 4.};
        double[] processTimes = {.5, 1., 2., 3., 4.};
        int[] maxQueues = {5, -1}; // -1 means no limit

        for (int maxQueue : maxQueues) {
            for (double createDelay : createTimes) {
                for (double processDelay : processTimes) {
                    System.out.printf("\n\n\nCreate delay: %.1f, Process delay: %.1f, Max queue: %d\n", createDelay,
                            processDelay, maxQueue);
                    Model model = createModel(createDelay, processDelay, maxQueue, false);
                    model.simulate(1000.);
                    model.printResult();
                }
            }
        }
    }
}
