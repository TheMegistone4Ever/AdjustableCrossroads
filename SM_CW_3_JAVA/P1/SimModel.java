package SM_CW_3_JAVA.P1;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.Create;
import SM_CW_3_JAVA.P1.simsimple.Model;
import SM_CW_3_JAVA.P1.simsimple.Path;
import SM_CW_3_JAVA.P1.simsimple.Process;

import java.util.ArrayList;

public class SimModel {
    public static void main(String[] args) {
        Create c = new Create("CREATOR", 1.);
        Process p = new Process("PROCESSOR", 1., 5, 1);
        System.out.printf("id0=%d id1=%d\n", c.getId(), p.getId());

        c.addPaths(
                new Path(p, 1.)
        );

        ArrayList<IElement> list = new ArrayList<>();
        list.add(c);
        list.add(p);
        final Model model = new Model(list, true);
        model.simulate(1000.);
        model.printResult();
    }
}
