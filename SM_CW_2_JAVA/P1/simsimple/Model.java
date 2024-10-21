package SM_CW_2_JAVA.P1.simsimple;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Model {
    private final Map<Integer, IElement> elements = new java.util.HashMap<>();
    double tNext, tCurr;
    int event;
    double epsilon = 1e-9;

    public Model(@NotNull ArrayList<IElement> elements) {
        for (IElement e : elements) {
            this.elements.put(e.getId(), e);
        }
        tNext = 0.0;
        event = 0;
        tCurr = tNext;
    }

    public void simulate(double time) {
        while (tCurr < time) {
            tNext = Double.MAX_VALUE;
            for (Map.Entry<Integer, IElement> entry : elements.entrySet()) {
                IElement e = entry.getValue();
                if (e.getTnext() < tNext) {
                    tNext = e.getTnext();
                    event = entry.getKey();
                }
            }
            System.out.println("\nIt's time for event in " +
                    elements.get(event).getName() +
                    ", time = " + tNext);
            for (IElement e : elements.values()) {
                e.doStatistics(tNext - tCurr);
            }
            tCurr = tNext;
            for (IElement e : elements.values()) {
                e.setTcurr(tCurr);
            }
            elements.get(event).outAct();
            for (IElement e : elements.values()) {
                if (!e.equals(elements.get(event))
                        && Math.abs(e.getTnext() - tCurr) < epsilon) {
                    e.outAct();
                }
            }
            printInfo();
        }
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

    protected Collection<IElement> getList() {
        return elements.values();
    }

    protected double getCurrentTime() {
        return tCurr;
    }
}
