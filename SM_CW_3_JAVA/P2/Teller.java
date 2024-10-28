package SM_CW_3_JAVA.P2;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.Channel;
import SM_CW_3_JAVA.P1.simsimple.ITask;
import SM_CW_3_JAVA.P1.simsimple.Process;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

class Teller extends Process {
    private int laneChanges = 0;

    public Teller(String nameOfElement, double delay, int maxQueue, int channels) {
        super(nameOfElement, delay, maxQueue, channels);
    }

    @Override
    public void inAct(ITask task) {
        if (checkLaneChange()) {
            incrementLaneChanges();
            changeLane(task);
        } else {
            Channel freeChannel = getFreeChannel();
            if (freeChannel != null) {
                setChannelBusy(freeChannel, task);
            } else {
                if (getQueue().size() < getMaxQueue()) {
                    getQueue().add(task);
                } else {
                    incFailures();
                }
            }
        }
    }

    public void addTasksToQueue(ITask @NotNull ... tasks) {
        Collections.addAll(getQueue(), tasks);
    }

    private boolean checkLaneChange() {
        int teller1Busy = totalBusy();
        int teller2Busy = getAnotherTeller().totalBusy();
        return teller1Busy > teller2Busy + 1 && teller1Busy + teller2Busy < 8;
    }

    private void changeLane(ITask task) {
        getAnotherTeller().inAct(task);
    }

    private @Nullable IElement getElementByName(String name) {
        return getParentModel().getList().stream()
                .filter(element -> element.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private Teller getAnotherTeller() {
        return getName().equals("TELLER_1")
                ? (Teller) getElementByName("TELLER_2")
                : (Teller) getElementByName("TELLER_1");
    }

    private int totalBusy() {
        return getQueue().size() + getState();
    }

    public int getLaneChanges() {
        return laneChanges;
    }

    public void incrementLaneChanges() {
        ++laneChanges;
    }
}
