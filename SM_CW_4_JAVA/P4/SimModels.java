package SM_CW_4_JAVA.P4;

import static SM_CW_4_JAVA.P2.SimModels.printTable;

public class SimModels {
    public static void main(String[] args) {
        final int[] w = {10, 13, 11};
        final int[] channels = {10};
        final int[] systemsCounts = {500, 1000, 1500, 2000, 2500, 3000, 3500};
        final int warnUps = 3;
        final int iterations = 5;
        printTable(w, channels, systemsCounts, warnUps, iterations);
    }
}
