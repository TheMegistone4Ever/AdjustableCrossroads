package SM_CW_2_JAVA.P4;

import SM_CW_2_JAVA.P1.simsimple.Create;
import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_2_JAVA.P2.simsimple.Model;
import SM_CW_2_JAVA.P2.simsimple.Process;
import org.jetbrains.annotations.NotNull;

import static SM_CW_2_JAVA.P3.SimModel.createModel;

public class SimModels {
    public static void main(String[] args) {
        final double[] createTimes = {.5, 1., 2., 3., 4.};
        final double[] processTimes = {.5, 1., 2., 3., 4.};
        final int[] maxQueues = {5, -1}; // -1 means no limit
        final int[] w = {16, 16, 20, 10, 20, 16, 21};
        final double time = 1000;

        for (int maxQueue : maxQueues) {
            System.out.printf("\n\nTable for Max Queue = %d\n", maxQueue);
            printTableBorder(w);
            System.out.printf(String.format("| %%-%ds | %%-%ds | %%-%ds | %%-%ds | %%-%ds | %%-%ds | %%-%ds |\n",
                            w[0] - 2, w[1] - 2, w[2] - 2, w[3] - 2, w[4] - 2, w[5] - 2, w[6] - 2),
                    "Create Delay", "Process Delay", "Current Time (ms)", "Failure", "Mean Queue Length",
                    "Mean Load", "Failure Probability");
            printTableBorder(w);

            for (double createDelay : createTimes) {
                for (double processDelay : processTimes) {
                    final Model model = createModel(createDelay, processDelay, maxQueue, false);
                    model.simulate(time);
                    for (IElement e : model.getList()) {
                        if (!(e.getClass().equals(SM_CW_2_JAVA.P1.simsimple.Process.class) || e.getClass().equals(Create.class))) {
                            Process p = (Process) e;
                            double simulationTime = e.getTCurr();
                            int failure = p.getFailure();
                            double meanQueueLength = p.getMeanQueue() / simulationTime;
                            double meanLoad = p.getMeanLoad() / simulationTime;
                            double failureProbability = p.getFailure() / (double) p.getQuantity();
                            System.out.printf(String.format("| %%-%d.1f | %%-%d.1f | %%-%d.2f | %%-%dd | %%-%d.6f " +
                                                    "| %%-%d.6f | %%-%d.6f |\n",
                                            w[0] - 2, w[1] - 2, w[2] - 2, w[3] - 2, w[4] - 2, w[5] - 2, w[6] - 2),
                                    createDelay, processDelay, simulationTime, failure, meanQueueLength, meanLoad,
                                    failureProbability);
                        }
                    }
                    printTableBorder(w);
                }
            }
        }
    }

    static void printTableBorder(int @NotNull [] columnWidths) {
        for (int width : columnWidths) {
            System.out.print("+");
            for (int i = 0; i < width; ++i) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }
}
