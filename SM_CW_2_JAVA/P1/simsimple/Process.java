package SM_CW_2_JAVA.P1.simsimple;

public class Process extends Element {
    private final int maxQueue;
    private int queue, failure;
    private double meanQueue;

    public Process(String nameOfElement, double delay, int maxQueue) {
        super(nameOfElement, delay);
        queue = 0;
        this.maxQueue = maxQueue >= 0 ? maxQueue : Integer.MAX_VALUE;
        meanQueue = .0;
    }

    @Override
    public void inAct() {
        if (super.getState() == 0) {
            super.setState(1);
            super.setTNext(super.getTCurr() + super.getDelay());
        } else {
            if (getQueue() < getMaxQueue()) {
                setQueue(getQueue() + 1);
            } else {
                ++failure;
            }
        }
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTNext(Double.MAX_VALUE);
        super.setState(0);

        if (super.getNextElement() != null) {
            super.getNextElement().inAct();
        }

        if (getQueue() > 0) {
            setQueue(getQueue() - 1);
            super.setState(1);
            super.setTNext(super.getTCurr() + super.getDelay());
        }
    }

    public int getFailure() {
        return failure;
    }

    public void incFailure() {
        ++failure;
    }

    public void addFailure(int failureToAdd) {
        failure += failureToAdd;
    }

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public int getMaxQueue() {
        return maxQueue;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.printf("\tfailure=%d", this.getFailure());
    }

    @Override
    public void doStatistics(double delta) {
        meanQueue += queue * delta;
    }

    public double getMeanQueue() {
        return meanQueue;
    }

    public void addMeanQueue(double meanQueueToAdd) {
        meanQueue += meanQueueToAdd;
    }
}
