package SM_CW_3_JAVA.P3;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.Channel;
import SM_CW_3_JAVA.P1.simsimple.ITask;
import SM_CW_3_JAVA.P1.simsimple.Path;
import SM_CW_3_JAVA.P1.simsimple.Process;

import java.util.ArrayDeque;
import java.util.HashMap;

public class TypeModifyingProcess extends Process {

    private HashMap<Integer, Integer> typeModifyingMap;

    public TypeModifyingProcess(String name, double delayMean, double delayDev, int channelsNum) {
        super(name, delayMean, delayDev, -1, channelsNum);
    }

    public void setTypeModifyingMap(int[] types, int[] modifiedTypes) {
        this.typeModifyingMap = new HashMap<>();
        for (int i = 0; i < types.length; ++i) {
            this.typeModifyingMap.put(types[i], modifiedTypes[i]);
        }
    }

    @Override
    public void outAct() {
        for (Channel channel : getSoonestChannels()) {
            ITask task = channel.getTask();

            Sick sick = (Sick) task;
            if (sick != null && typeModifyingMap.get(sick.getType()) != null) {
                sick.setType(typeModifyingMap.get(sick.getType()));
            }

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
                setChannelBusy(channel, queue.poll());
            }
        }
    }
}
