package SM_CW_3_JAVA.P1.simsimple;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Process extends Element {
    private final int maxQueue;
    private final ArrayDeque<Task> queue = new ArrayDeque<>();
    private final ArrayList<Channel> channels = new ArrayList<>();
    private int failures = 0;
    private double accumulatedLoad = .0;
    private double accumulatedQueue = .0;
    private double totalLeaveTime = .0;
    private double previousLeaveTime = .0;

    public Process(String nameOfElement, double delay, int maxQueue, int channels) {
        super(nameOfElement, delay);
        this.maxQueue = maxQueue >= 0 ? maxQueue : Integer.MAX_VALUE;
        for (int i = 0; i < channels; ++i) {
            this.channels.add(new Channel());
        }
    }

    public void inAct(Task task) {
        Channel freeChannel = getFreeChannel();
        if (freeChannel != null) {
            freeChannel.setTask(task);
            freeChannel.setTNext(super.getTCurr() + super.getDelay());
            super.addState(1);
        } else {
            if (queue.size() < maxQueue) {
                queue.add(task);
            } else {
                ++failures;
            }
        }
    }

    @Override
    public void outAct() {
        for (Channel channel : getSoonestChannels()) {
            Task task = channel.getTask();

            Path toNext = getNextPath(task);
            if (toNext.isBlocked(task)) {
                continue;
            }

            Element e = toNext.getTo();
            if (e != null) {
                e.inAct(task);
            }

            super.incQuantity();

            channel.setTNext(Double.MAX_VALUE);
            channel.setTask(null);
            super.addState(-1);
            totalLeaveTime += super.getTCurr() - previousLeaveTime;
            previousLeaveTime = super.getTCurr();

            if (!queue.isEmpty()) {
                channel.setTask(queue.poll());
                channel.setTNext(super.getTCurr() + super.getDelay());
                super.addState(1);
            }
        }
    }

    public int getFailures() {
        return failures;
    }

    public void addFailure(int failureToAdd) {
        failures += failureToAdd;
    }

    public ArrayDeque<Task> getQueue() {
        return queue;
    }

    private Channel getFreeChannel() {
        return channels.stream()
                .filter(channel -> !channel.getState())
                .findFirst()
                .orElse(null);
    }

    @Override
    public double getTNext() {
        return channels.stream()
                .filter(Channel::getState)
                .map(Channel::getTNext)
                .min(Double::compareTo)
                .orElse(Double.MAX_VALUE);
    }

    protected List<Channel> getSoonestChannels() {
        double epsilon = 1e-6;
        return channels.stream()
                .filter(channel -> channel.getState() && Math.abs(channel.getTNext() - getTNext()) < epsilon)
                .toList();
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.printf("\tfailure=%d", failures);
    }

    @Override
    public int getState() {
        return channels.stream()
                .noneMatch(Channel::getState)
                ? 0 : 1;
    }

    @Override
    public void doStatistics(double delta) {
        accumulatedQueue += queue.size() * delta;
        accumulatedLoad += getState() * delta;
    }

    public double getAccumulatedQueue() {
        return accumulatedQueue;
    }

    public double getAccumulatedLoad() {
        return accumulatedLoad;
    }
}
