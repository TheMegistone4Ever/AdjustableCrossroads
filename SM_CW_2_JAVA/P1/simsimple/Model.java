package SM_CW_2_JAVA.P1.simsimple;

import java.util.ArrayList;

public class Model {
    private final ArrayList<IElement> list;
    double tNext, tCurr;
    int event;
    double epsilon = 1e-9;

    public Model(ArrayList<IElement> elements) {
        list = elements;
        tNext = 0.0;
        event = 0;
        tCurr = tNext;
    }

    public void simulate(double time) {
        while (tCurr < time) {
            tNext = Double.MAX_VALUE;
            for (IElement e : list) {
                if (e.getTnext() < tNext) {
                    tNext = e.getTnext();
                    event = e.getId();
                }
            }
            System.out.println("\nIt's time for event in " +
                    list.get(event).getName() +
                    ", time = " + tNext);
            for (IElement e : list) {
                e.doStatistics(tNext - tCurr);
            }
            tCurr = tNext;
            for (IElement e : list) {
                e.setTcurr(tCurr);
            }
            list.get(event).outAct();
            for (IElement e : list) {
                if (!e.equals(list.get(event))
                        && Math.abs(e.getTnext() - tCurr) < epsilon) {
                    e.outAct();
                }
            }
            printInfo();
        }

        // if any remains in queue, add to failure statistics
        for (IElement e : list) {
            if (e.getClass().equals(Process.class)) {
                Process p = (Process) e;
                p.addFailure(p.getQueue() + p.getState());
            }
        }
    }
    public void printInfo() {
        for (IElement e : list) {
            e.printInfo();
        }
    }
    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (IElement e : list) {
            e.printResult();
            if (e.getClass().equals(Process.class)) {
                Process p = (Process) e;
                System.out.println(
                        "failure = " + p.getFailure() +
                        "\nmean length of queue = " +
                        p.getMeanQueue() / tCurr +
                        "\nfailure probability = " +
                        p.getFailure() / (double) p.getQuantity()
                 + "\n"
                );
            }
        }
    }

    protected ArrayList<IElement> getList() {
        return list;
    }

    protected double getCurrentTime() {
        return tCurr;
    }
}
