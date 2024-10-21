package SM_CW_2_JAVA.P1;

import SM_CW_2_JAVA.P1.simsimple.*;
import SM_CW_2_JAVA.P1.simsimple.Process;

import java.util.ArrayList;

public class SimModel {
    public static void main(String[] args) {
        Create c = new Create(2.0);
        Process p = new Process(1.0);
        System.out.println("id0 = " + c.getId() + " id1=" + p.getId());
        c.setNextElement(p);
        p.setMaxQueue(5);
        c.setName("CREATOR");
        p.setName("PROCESSOR");
        c.setDistribution("exp");
        p.setDistribution("exp");
        ArrayList<IElement> list = new ArrayList<>();
        list.add(c);
        list.add(p);
        Model model = new Model(list);
        model.simulate(1000.0);
        model.printResult();
    }
}
