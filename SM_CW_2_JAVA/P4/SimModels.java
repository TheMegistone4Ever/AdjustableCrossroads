package SM_CW_2_JAVA.P4;


import SM_CW_2_JAVA.P2.simsimple.Model;

import static SM_CW_2_JAVA.P3.SimModel.createModel;

public class SimModels {
    public static void main(String[] args) {
        double[] createTimes = {.5, 1.0, 2.0, 3.0, 4.0};
        double[] processTimes = {.5, 1.0, 2.0, 3.0, 4.0};
        int[] maxQueues = {5, -1}; // -1 means no limit

        for (int maxQueue : maxQueues) {
            for (double createDelay : createTimes) {
                for (double processDelay : processTimes) {
                    Model model = createModel(createDelay, processDelay, maxQueue);
                    model.simulate(3000.0);
                    model.printResult();
                }
            }
        }
    }
}
