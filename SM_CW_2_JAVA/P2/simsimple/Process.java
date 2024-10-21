package SM_CW_2_JAVA.P2.simsimple;

public class Process extends SM_CW_2_JAVA.P1.simsimple.Process {
    private double meanLoad;

    public Process() {
        super();

        meanLoad = .0;
    }

    public Process(int maxQueue) {
        super(maxQueue);

        meanLoad = .0;
    }

    public Process(double delay) {
        super(delay);

        meanLoad = .0;
    }

    public Process(double delay, int maxQueue) {
        super(delay, maxQueue);

        meanLoad = .0;
    }

    public Process(String nameOfElement, double delay) {
        super(nameOfElement, delay);

        meanLoad = .0;
    }

    public Process(String nameOfElement, double delay, int maxQueue) {
        super(nameOfElement, delay, maxQueue);

        meanLoad = .0;
    }

    @Override
    public void doStatistics(double delta) {
        super.doStatistics(delta);

        meanLoad += super.getState() * delta;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.printf("\n\tmeanLoad=%.6f\n", this.getMeanLoad());
    }

    public double getMeanLoad() {
        return meanLoad;
    }

    public void addMeanLoad(double meanLoadToAdd) {
        meanLoad += meanLoadToAdd;
    }
}
