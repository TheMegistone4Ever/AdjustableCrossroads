package SM_CW_3_JAVA.P1.simsimple;

public class Task implements ITask {
    private double tNext = Double.MAX_VALUE;

    public double getTNext() {
        return tNext;
    }

    public void setTNext(double tNext) {
        this.tNext = tNext;
    }
}
