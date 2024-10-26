package SM_CW_2_JAVA.P1.simsimple;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Model {
    private final boolean verbose;
    private final Map<Integer, IElement> elements = new java.util.HashMap<>();
    private double tNext, tCurr;
    private int event;

    public Model(@NotNull ArrayList<IElement> elements, boolean verbose) {
        for (IElement e : elements) {
            this.elements.put(e.getId(), e);
        }
        tNext = .0;
        event = 0;
        tCurr = tNext;
        this.verbose = verbose;
    }

    public void simulate(double time) {
        while (tCurr < time) {
            tNext = Double.MAX_VALUE;
            for (Map.Entry<Integer, IElement> entry : elements.entrySet()) {
                IElement e = entry.getValue();
                if (e.getTNext() < tNext) {
                    tNext = e.getTNext();
                    event = entry.getKey();
                }
            }

            if (verbose) {
                System.out.printf("\nIt's time for event in %s, time=%.6f\n", elements.get(event).getName(), tNext);
            }

            for (IElement e : elements.values()) {
                e.doStatistics(tNext - tCurr);
            }
            tCurr = tNext;
            for (IElement e : elements.values()) {
                e.setTCurr(tCurr);
            }

            for (IElement e : elements.values()) {
                double epsilon = 1e-6;
                if (Math.abs(e.getTNext() - tCurr) < epsilon) {
                    e.outAct();
                }
            }

            if (verbose) {
                printInfo();
            }
        }

        // if any remains in queue, add to failure statistics
        for (IElement e : elements.values()) {
            if (e.getClass().equals(Process.class)) {
                Process p = (Process) e;
                p.addFailure(p.getQueue() + p.getState());
            }
        }
    }

    public void printInfo() {
        for (IElement e : elements.values()) {
            e.printInfo();
        }
    }

    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (IElement e : elements.values()) {
            e.printResult();
            if (e.getClass().equals(Process.class)) {
                Process p = (Process) e;
                System.out.printf("failure=%d\nmean length of queue=%.6f\nfailure probability=%.6f\n",
                        p.getFailure(), p.getMeanQueue() / tCurr, p.getFailure() / (double) p.getQuantity());
            }
        }
    }

    public Collection<IElement> getList() {
        return elements.values();
    }

    public double getCurrentTime() {
        return tCurr;
    }
}
