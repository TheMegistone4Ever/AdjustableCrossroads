package SM_CW_4_JAVA.P2;

import SM_CW_3_JAVA.P1.simsimple.Model;
import org.jetbrains.annotations.NotNull;

import static SM_CW_2_JAVA.P4.SimModels.printTableBorder;
import static SM_CW_4_JAVA.P1.SimModel.createModel;

public class SimModels {
    public static void main(String[] args) {
        final int[] w = {10, 13, 11};
        final int[] channels = {1};
        final int[] systemsCounts = {500, 1000, 1500, 2000, 2500, 3000, 3500};
        final int warnUps = 3;
        final int iterations = 5;
        printTable(w, channels, systemsCounts, warnUps, iterations);
    }

    public static void printTable(int[] w, int @NotNull [] channels, int[] systemsCounts, int warnUps, int iterations) {
        printTableBorder(w);
        System.out.printf(String.format("| %%-%ds | %%-%ds | %%-%ds |\n", w[0] - 2, w[1] - 2, w[2] - 2),
                "Channels", "N (systems)", "Time (ns)");
        printTableBorder(w);

        for (int channelsCount : channels) {
            for (int systemsCount : systemsCounts) {
                Model model = createModel(channelsCount, systemsCount, false);

                for (int i = 0; i < warnUps; ++i) {
                    model.simulate(1000.);
                }

                long start = System.nanoTime();
                for (int i = 0; i < iterations; ++i) {
                    model.simulate(1000.);
                }
                long end = System.nanoTime();

                System.out.printf(String.format("| %%-%ds | %%-%ds | %%-%ds |\n",
                        w[0] - 2, w[1] - 2, w[2] - 2), channelsCount, systemsCount, (end - start) / iterations);
            }

            printTableBorder(w);
        }
    }
}
