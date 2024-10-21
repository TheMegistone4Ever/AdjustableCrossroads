package SM_CW_2_JAVA.P2.simsimple;

public class Process extends SM_CW_2_JAVA.P1.simsimple.Process {
    private double meanLoad;

    public Process(double delay) {
        super(delay);

        meanLoad = 0.0;
    }

    @Override
    public void doStatistics(double delta) {
        super.doStatistics(delta);

        meanLoad += super.getState() * delta;
    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("meanLoad = " + this.getMeanLoad());
    }
    public double getMeanLoad() {
        return meanLoad;
    }
    public void addMeanLoad(double meanLoadToAdd) {
        meanLoad += meanLoadToAdd;
    }
}
