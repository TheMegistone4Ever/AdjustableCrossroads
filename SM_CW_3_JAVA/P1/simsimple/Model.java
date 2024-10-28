package SM_CW_3_JAVA.P1.simsimple;

import SM_CW_2_JAVA.P1.simsimple.IElement;

import java.util.ArrayList;

public class Model extends SM_CW_2_JAVA.P1.simsimple.Model {

    public Model(ArrayList<IElement> elements, boolean verbose) {
        super(elements, verbose);
    }

    @Override
    public void simulate(double time) {
        super.simulate(time);

        // if any remains in queue, add to failure statistics
        for (IElement e : super.getList()) {
            if (e instanceof Process p) {
                p.addFailures(p.getQueue().size() + p.getState());
            }
        }
    }

    @Override
    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (IElement e : super.getList()) {
            e.printResult();
            if (e instanceof Process p) {
                System.out.printf("""
                                failure = %d
                                mean length of queue = %.6f
                                mean load = %.6f
                                mean processing time = %.6f
                                failure probability = %.6f
                                """,
                        p.getFailures(),
                        p.getAccumulatedQueue() / super.getCurrentTime(),
                        p.getAccumulatedLoad() / super.getCurrentTime(),
                        p.getAccumulatedProcessingTime() / p.getQuantity(),
                        p.getFailures() / (double) p.getQuantity()
                );
            }
        }
    }
}
