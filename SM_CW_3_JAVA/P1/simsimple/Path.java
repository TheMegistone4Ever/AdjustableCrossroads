package SM_CW_3_JAVA.P1.simsimple;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class Path implements Comparable<Path> {
    private final Element to;
    private int priority = 1;
    private double probability = 1.;
    private Predicate<Task> blocker = null;

    public Path() {
        this.to = null;
    }

    public Path(Element to, double probability) {
        this.to = to;
        this.probability = probability;
    }

    public Path(Element to, int priority) {
        this.to = to;
        this.priority = priority;
    }

    public Path(Element to, double probability, int priority) {
        this.to = to;
        this.probability = probability;
        this.priority = priority;
    }

    public Path(Element to, double probability, int priority, Predicate<Task> blocker) {
        this.to = to;
        this.probability = probability;
        this.priority = priority;
        this.blocker = blocker;
    }

    public boolean isBlocked(Task task) {
        return blocker != null && blocker.test(task);
    }

    public Element getTo() {
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
