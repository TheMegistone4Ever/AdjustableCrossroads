package SM_CW_4_JAVA.P1;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.Create;
import SM_CW_3_JAVA.P1.simsimple.Model;
import SM_CW_3_JAVA.P1.simsimple.Path;
import SM_CW_3_JAVA.P1.simsimple.Process;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public class SimModel {
    public static void main(String[] args) {
        final Model model = createModel(100, 1, false);
        long start = System.currentTimeMillis();
        model.simulate(1000.);
        long end = System.currentTimeMillis();
        System.out.printf("Simulation time: %d ms\n", end - start);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull Model createModel(int systemsCount, int channels, boolean verbose) {
        Create c = new Create("CREATOR", 1.);
        ArrayDeque<IElement> list = new ArrayDeque<>(systemsCount + 1);
        list.add(c);
        list.add(new Process("PROCESSOR", .1, 5, channels));
        for (int i = 1; i < systemsCount; ++i) {
            Process p = new Process("PROCESSOR", .1, 5, channels);
            list.add(p);
        }
        Iterator<IElement> iterator = list.iterator();
        IElement prev = iterator.next();

        while (iterator.hasNext()) {
            IElement next = iterator.next();
            prev.addPaths(new Path(next));
            prev = next;
        }
        if (verbose) {
            System.out.printf("id0=%d", c.getId());
            int i = 0;
            for (IElement iElement : list) {
                System.out.printf(" id%d=%d", ++i, iElement.getId());
            }
        }
        return new Model(new ArrayList<>(list), verbose);
    }
}
