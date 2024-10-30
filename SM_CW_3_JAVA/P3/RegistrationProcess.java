package SM_CW_3_JAVA.P3;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.Channel;
import SM_CW_3_JAVA.P1.simsimple.ITask;
import SM_CW_3_JAVA.P1.simsimple.Path;
import SM_CW_3_JAVA.P1.simsimple.Process;

import java.util.ArrayDeque;
import java.util.HashMap;

public class RegistrationProcess extends Process {
    private int prioritizedPatientType;
    private HashMap<Integer, Double> patientTypedDelays;

    public RegistrationProcess(String name, double delayMean, int channelsNum) {
        super(name, delayMean, -1, channelsNum);
    }

    public void setPrioritizedPatientType(int type) {
        this.prioritizedPatientType = type;
    }

    public void setPatientTypedDelays(int[] types, double[] delays) {
        this.patientTypedDelays = new HashMap<>();
        for (int i = 0; i < types.length; ++i) {
            this.patientTypedDelays.put(types[i], delays[i]);
        }
    }

    @Override
    public void inAct(ITask task) {
        Channel freeChannel = getFreeChannel();
        if (freeChannel != null) {
            freeChannel.setTask(task);
            double originalDelayMean = getDelayMean();
            int patientType = ((Patient) task).getType();
            setDelayMean(patientTypedDelays.get(patientType));
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
        sortQueueByPatientPriority();
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
                Patient patient = (Patient) queue.poll();
                int type = patient.getType();
                setDelayMean(patientTypedDelays.get(type));
                setChannelBusy(channel, patient);
                setDelayMean(originalDelay);
            }
        }
    }

    private void sortQueueByPatientPriority() {
        ArrayDeque<Patient> prioritizedPatients = new ArrayDeque<>();
        ArrayDeque<Patient> otherPatients = new ArrayDeque<>();
        ArrayDeque<ITask> queue = getQueue();
        while (!queue.isEmpty()) {
            Patient patient = (Patient) queue.poll();
            if (patient.getType() == prioritizedPatientType) {
                prioritizedPatients.add(patient);
            } else {
                otherPatients.add(patient);
            }
        }
        queue.addAll(prioritizedPatients);
        queue.addAll(otherPatients);
    }
}
