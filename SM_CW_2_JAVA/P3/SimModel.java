package SM_CW_2_JAVA.P3;


import SM_CW_2_JAVA.P1.simsimple.Create;
import SM_CW_2_JAVA.P1.simsimple.Element;
import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_2_JAVA.P2.simsimple.Model;
import SM_CW_2_JAVA.P2.simsimple.Process;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class SimModel {
    public static void main(String[] args) {
        Model model = createModel(4.0, 1.0, 5);
        model.simulate(3000.0);
        model.printResult();
    }

    @Contract("_, _, _ -> new")
    public static @NotNull Model createModel(double cDelay, double pDelay, int pMaxQueue) {
        Create c = new Create(cDelay);
        c.setName("CREATOR");
        c.setDistribution("exp");
        Process[] processes = new Process[3];
        for (int i = 0; i < processes.length; ++i) {
            processes[i] = new Process(pDelay);
            processes[i].setMaxQueue(pMaxQueue);
            processes[i].setDistribution("exp");
            processes[i].setName("PROCESSOR_" + (i + 1));
        }
        System.out.print("id0 = " + c.getId());
        for (int i = 0; i < processes.length; ++i) {
            System.out.print(" id" + (i + 1) + "=" + processes[i].getId());
        }
        c.setNextElement(processes[0]);
        for (int i = 0; i < processes.length - 1; ++i) {
            processes[i].setNextElement(processes[i + 1]);
        }
        ArrayList<IElement> list = new ArrayList<>();
        list.add(c);
        Collections.addAll(list, processes);
        return new Model(list);
    }
}
