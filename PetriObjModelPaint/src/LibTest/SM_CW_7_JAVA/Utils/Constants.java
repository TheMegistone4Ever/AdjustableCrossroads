package LibTest.SM_CW_7_JAVA.Utils;

public abstract class Constants {
    public static final int[] SIMULATION_TIME = {10_000, 600};

    public static final int GENERATOR_TIME_MEAN = 40;
    public static final double[] MOVE_TIMES = {6., 7., 5.};
    public static final double[] PROCESS_TIMES = {60., 100.};
    public static final String[] PROCESS_DISTRIBUTIONS = {"norm", "exp"};

    public static final int BUS_CAPACITY = 25;
    public static final double[] BUS_INTERVALS = {20., 30.};
    public static final int[] BUS_INITIAL_COUNTS = {1, 1};
}
