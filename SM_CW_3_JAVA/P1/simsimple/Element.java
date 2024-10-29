package SM_CW_3_JAVA.P1.simsimple;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Element extends SM_CW_2_JAVA.P1.simsimple.Element {
    private final PriorityQueue<Path> fork = new PriorityQueue<>();
    private Forking forking = Forking.COMBINED;

    public Element(String nameOfElement, double delay) {
        super(nameOfElement, delay);
    }

    public Element(String nameOfElement, double delay, Forking forking) {
        super(nameOfElement, delay);
        this.forking = forking;
    }

    public Element(String nameOfElement, double delayMean, double delayDev) {
        super(nameOfElement, delayMean, delayDev);
    }

    public Element(String name) {
        super(name);
    }

    public Path getNextPath(ITask task) {
        if (fork.isEmpty()) {
            return new Path();
        }
        PriorityQueue<Path> unblockedPaths = getUnblockedPaths(task);
        return unblockedPaths.isEmpty() ? fork.peek() : switch (forking) {
            case PROBABILISTIC -> getNextProbabilistic(unblockedPaths);
            case PRIORITIZED -> getNextPriority(unblockedPaths);
            default -> getNextCombined(unblockedPaths);
        };
    }

    private Path getNextProbabilistic(@NotNull PriorityQueue<Path> unblockedPaths) {
        double random = Math.random();
        double accumulatedProbability = 0;
        double sum = unblockedPaths.stream().mapToDouble(Path::getProbability).sum();
        for (Path path : unblockedPaths) {
            accumulatedProbability += path.getProbability() / sum;
            if (random < accumulatedProbability) {
                return path;
            }
        }
        return unblockedPaths.peek();
    }

    private Path getNextPriority(@NotNull PriorityQueue<Path> unblockedPaths) {
        return unblockedPaths.peek();
    }

    private Path getNextCombined(@NotNull PriorityQueue<Path> unblockedPaths) {
        PriorityQueue<Path> withMaxPriority = new PriorityQueue<>();
        int maxPriority = unblockedPaths.stream()
                .mapToInt(Path::getPriority)
                .max()
                .orElse(1);
        for (Path path : unblockedPaths) {
            if (path.getPriority() == maxPriority) {
                withMaxPriority.add(path);
            }
        }
        return getNextProbabilistic(withMaxPriority);
    }

    @Contract("_ -> new")
    private @NotNull PriorityQueue<Path> getUnblockedPaths(ITask task) {
        return fork.stream()
                .filter(path -> !path.isBlocked(task))
                .collect(Collectors.toCollection(PriorityQueue::new));
    }

    public void addPaths(Path... paths) {
        Collections.addAll(fork, paths);
    }

    public void setForking(Forking forking) {
        this.forking = forking;
    }
}
