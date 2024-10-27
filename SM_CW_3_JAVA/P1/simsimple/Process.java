package SM_CW_3_JAVA.P1.simsimple;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static SM_CW_3_JAVA.P1.simsimple.constants.epsilon;

public class Process extends Element {
    private final int maxQueue;
    private final ArrayDeque<Task> queue = new ArrayDeque<>();
    private final ArrayList<Channel> channels;
    private int failures = 0;
    private double accumulatedLoad = .0;
    private double accumulatedQueue = .0;
    private double accumulatedProcessingTime = .0;
    private double previousLeaveTime = .0;

    public Process(String nameOfElement, double delay, int maxQueue, int channels) {
        super(nameOfElement, delay);
        this.maxQueue = maxQueue >= 0 ? maxQueue : Integer.MAX_VALUE;
        this.channels = new ArrayList<>(channels);
        for (int i = 0; i < channels; ++i) {
            this.channels.add(new Channel());
        }
    }

    public void inAct(Task task) {
        Channel freeChannel = getFreeChannel();
        if (freeChannel != null) {
            setChannelBusy(freeChannel, task);
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

            Element to = toNext.getTo();
            if (to != null) {
                to.inAct(task);
            }

            super.incQuantity();
            accumulatedProcessingTime += super.getTCurr() - previousLeaveTime;
            previousLeaveTime = super.getTCurr();

            setChannelFree(channel);
            if (!queue.isEmpty()) {
                setChannelBusy(channel, queue.poll());
            }
        }
    }

    private void setChannelBusy(@NotNull Channel channel, Task task) {
        channel.setTask(task);
        channel.setTNext(super.getTCurr() + super.getDelay());
        super.addState(1);
    }

    private void setChannelFree(@NotNull Channel channel) {
        channel.setTask(null);
        channel.setTNext(Double.MAX_VALUE);
        super.addState(-1);
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

    public double getAccumulatedProcessingTime() {
        return accumulatedProcessingTime;
    }
}
