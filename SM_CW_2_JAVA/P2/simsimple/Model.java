package SM_CW_2_JAVA.P2.simsimple;

import SM_CW_2_JAVA.P1.simsimple.Create;
import SM_CW_2_JAVA.P1.simsimple.Element;
import SM_CW_2_JAVA.P1.simsimple.IElement;

import java.util.ArrayList;

public class Model extends SM_CW_2_JAVA.P1.simsimple.Model {

    public Model(ArrayList<IElement> elements) {
        super(elements);
    }

    @Override
    public void simulate(double time) {
        super.simulate(time);

        // if any remains in queue, add to failure statistics
        for (IElement e : super.getList()) {
            if (!(e.getClass().equals(SM_CW_2_JAVA.P1.simsimple.Process.class)
                    || e.getClass().equals(Create.class))) {
                Process p = (Process) e;
                p.addFailure(p.getQueue() + p.getState());
            }
        }
    }

    @Override
    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (IElement e : super.getList()) {
            e.printResult();
                if (!(e.getClass().equals(SM_CW_2_JAVA.P1.simsimple.Process.class)
                        || e.getClass().equals(Create.class))) {
                Process p = (Process) e;
                System.out.println(
                        "failure = " + p.getFailure() +
                                "\nmean length of queue = " +
                                p.getMeanQueue() / super.getCurrentTime() +
                                "\nmean load = " +
                                p.getMeanLoad() / super.getCurrentTime() +
                                "\nfailure probability = " +
                                p.getFailure() / (double) p.getQuantity()
                                + "\n"
                );
            }
        }
    }
}
