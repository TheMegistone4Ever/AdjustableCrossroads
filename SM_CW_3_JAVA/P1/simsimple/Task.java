package SM_CW_3_JAVA.P1.simsimple;

public class Task implements ITask {
    private static int nextId = -1;
    private final int id = ++nextId;
    private double tNext = Double.MAX_VALUE;
    private double timeIn = 0;

    public Task() {
    }

    public Task(double timeIn) {
        this.timeIn = timeIn;
        tNext = timeIn;
    }

    public double getTNext() {
        return tNext;
    }

    public void setTNext(double tNext) {
        this.tNext = tNext;
    }

    @Override
    public double getTimeIn() {
        return timeIn;
    }

    @Override
    public int getId() {
        return id;
    }
}
