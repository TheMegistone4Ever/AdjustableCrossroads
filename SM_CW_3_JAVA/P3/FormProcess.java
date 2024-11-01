package SM_CW_3_JAVA.P3;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.Channel;
import SM_CW_3_JAVA.P1.simsimple.ITask;
import SM_CW_3_JAVA.P1.simsimple.Path;
import SM_CW_3_JAVA.P1.simsimple.Process;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashMap;

public class FormProcess extends Process {
    private int prioritizedSickType;
    private HashMap<Integer, Double> sickTypedDelays;

    public FormProcess(String name, double delayMean, int channelsNum) {
        super(name, delayMean, -1, channelsNum);
    }

    public void setPrioritizedSickType(int type) {
        this.prioritizedSickType = type;
    }

    public void setSickTypedDelays(int @NotNull [] types, double[] delays) {
        this.sickTypedDelays = new HashMap<>();
        for (int i = 0; i < types.length; ++i) {
            this.sickTypedDelays.put(types[i], delays[i]);
        }
    }

    @Override
    public void inAct(ITask task) {
        if (task == null) {
            return;
        }
        Channel freeChannel = getFreeChannel();
        if (freeChannel != null) {
            freeChannel.setTask(task);
            double originalDelayMean = getDelayMean();
            int sickType = ((Sick) task).getType();
            setDelayMean(sickTypedDelays.get(sickType));
            freeChannel.setTNext(super.getTCurr() + super.getDelay());
            setDelayMean(originalDelayMean);
        } else {
            ArrayDeque<ITask> queue = getQueue();
            if (queue.size() < getMaxQueue()) {
                queue.add(task);
            } else {
                incFailures();
            }
        }
    }

    @Override
    public void outAct() {
        double originalDelay = getDelayMean();
        sortQueueBySickPriority();
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
            addAccumulatedProcessingTime(super.getTCurr() - getPreviousLeaveTime());
            setPreviousLeaveTime(super.getTCurr());

            setChannelFree(channel);
            ArrayDeque<ITask> queue = getQueue();
            if (!queue.isEmpty()) {
                Sick sick = (Sick) queue.poll();
                int type = sick.getType();
                setDelayMean(sickTypedDelays.get(type));
                setChannelBusy(channel, sick);
                setDelayMean(originalDelay);
            }
        }
    }

    private void sortQueueBySickPriority() {
        ArrayDeque<Sick> prioritizedSicks = new ArrayDeque<>();
        ArrayDeque<Sick> otherSicks = new ArrayDeque<>();
        ArrayDeque<ITask> queue = getQueue();
        while (!queue.isEmpty()) {
            Sick sick = (Sick) queue.poll();
            if (sick.getType() == prioritizedSickType) {
                prioritizedSicks.add(sick);
            } else {
                otherSicks.add(sick);
            }
        }
        queue.addAll(prioritizedSicks);
        queue.addAll(otherSicks);
    }
}
