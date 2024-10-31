package SM_CW_3_JAVA.P1.simsimple;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class Path implements Comparable<Path> {
    private final IElement to;
    private int priority = 1;
    private double probability = 1.;
    private Predicate<ITask> blocker = null;

    public Path() {
        this.to = null;
    }

    public Path(IElement to) {
        this.to = to;
    }

    public Path(IElement to, double probability) {
        this.to = to;
        this.probability = probability;
    }

    public Path(IElement to, double probability, int priority) {
        this.to = to;
        this.probability = probability;
        this.priority = priority;
    }

    public Path(IElement to, double probability, int priority, Predicate<ITask> blocker) {
        this.to = to;
        this.probability = probability;
        this.priority = priority;
        this.blocker = blocker;
    }

    public Path(IElement to, int priority, Predicate<ITask> blocker) {
        this.to = to;
        this.priority = priority;
        this.blocker = blocker;
    }

    public boolean isBlocked(ITask task) {
        if (task == null) {
            return false;
        }
        return blocker != null && blocker.test(task);
    }

    public IElement getTo() {
        return to;
    }

    public int getPriority() {
        return priority;
    }

    public double getProbability() {
        return probability;
    }

    @Override
    public int compareTo(@NotNull Path o) {
        return Integer.compare(o.priority, priority);
    }
}
