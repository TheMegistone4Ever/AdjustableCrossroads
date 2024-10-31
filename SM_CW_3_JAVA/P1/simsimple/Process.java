package SM_CW_3_JAVA.P1.simsimple;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static SM_CW_3_JAVA.P1.simsimple.constants.epsilon;

public class Process extends Element {
    private final int maxQueue;
    private final ArrayDeque<ITask> queue = new ArrayDeque<>();
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
        this.channels.addAll(Collections.nCopies(channels, new Channel()));
    }

    public Process(String nameOfElement, double delayMean, double delayDev, int maxQueue, int channels) {
        super(nameOfElement, delayMean, delayDev);
        this.maxQueue = maxQueue >= 0 ? maxQueue : Integer.MAX_VALUE;
        this.channels = new ArrayList<>(channels);
        this.channels.addAll(Collections.nCopies(channels, new Channel()));
    }

    public void inAct(ITask task) {
        Channel freeChannel = getFreeChannel();
        if (freeChannel != null) {
            setChannelBusy(freeChannel, task);
        } else {
            if (queue.size() < maxQueue) {
                if (task != null) {
                    queue.add(task);
                }
            } else {
                ++failures;
            }
        }
    }

    @Override
    public void outAct() {
        for (Channel channel : getSoonestChannels()) {
            ITask task = channel.getTask();

            Path toNext = getNextPath(task);
            if (toNext.isBlocked(task)) {
                continue;
            }

            IElement to = toNext.getTo();
            if (to != null) {
                to.setTNext(super.getTCurr());
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

    public void setChannelBusy(@NotNull Channel channel, ITask task) {
        channel.setTask(task);
        channel.setTNext(super.getTCurr() + super.getDelay());
        super.addState(1);
    }

    protected void setChannelFree(@NotNull Channel channel) {
        channel.setTask(null);
        channel.setTNext(Double.MAX_VALUE);
        super.addState(-1);
    }

    public int getFailures() {
        return failures;
    }

    public void addFailures(int failureToAdd) {
        failures += failureToAdd;
    }

    public void incFailures() {
        ++failures;
    }

    public ArrayDeque<ITask> getQueue() {
        return queue;
    }

    protected Channel getFreeChannel() {
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

    @Override
    public void setTNext(double tNext) {
        double previousTNext = getTNext();
        for (Channel channel : channels) {
            if (Math.abs(channel.getTNext() - previousTNext) < epsilon) {
                channel.setTNext(tNext);
            }
        }
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

    public int getMaxQueue() {
        return maxQueue;
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

    public void addAccumulatedProcessingTime(double timeToAdd) {
        accumulatedProcessingTime += timeToAdd;
    }

    public double getPreviousLeaveTime() {
        return previousLeaveTime;
    }

    public void setPreviousLeaveTime(double previousLeaveTime) {
        this.previousLeaveTime = previousLeaveTime;
    }
}
