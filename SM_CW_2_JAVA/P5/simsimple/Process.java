package SM_CW_2_JAVA.P5.simsimple;

import java.util.ArrayList;
import java.util.List;

public class Process extends SM_CW_2_JAVA.P2.simsimple.Process {
    private final ArrayList<Channel> channels = new ArrayList<>();

    public Process(String nameOfElement, double delay, int maxQueue) {
        super(nameOfElement, delay, maxQueue);
        channels.add(new Channel());
    }

    @Override
    public void inAct() {
        Channel freeChannel = getFreeChannel();
        if (freeChannel != null) {
            freeChannel.setState(1);
            freeChannel.setTNext(super.getTCurr() + super.getDelay());
            addState(1);
        } else {
            if (getQueue() < getMaxQueue()) {
                setQueue(getQueue() + 1);
            } else {
                super.incFailure();
            }
        }
    }

    @Override
    public void outAct() {
        List<Channel> soonestChannels = getSoonestChannels();
        super.addQuantity(soonestChannels.size());

        if (super.getNextElement() != null) {
            super.getNextElement().inAct();
        }

        for (Channel channel : soonestChannels) {
            channel.setTNext(Double.MAX_VALUE);
            channel.setState(0);
            addState(-1);
            if (getQueue() > 0) {
                setQueue(getQueue() - 1);
                channel.setState(1);
                channel.setTNext(super.getTCurr() + super.getDelay());
                addState(1);
            }
        }
    }

    private Channel getFreeChannel() {
        return channels.stream()
                .filter(channel -> channel.getState() == 0)
                .findFirst()
                .orElse(null);
    }

    @Override
    public double getTNext() {
        return channels.stream()
                .filter(channel -> channel.getState() == 1)
                .map(Channel::getTNext)
                .min(Double::compareTo)
                .orElse(Double.MAX_VALUE);
    }

    protected List<Channel> getSoonestChannels() {
        double minTNext = this.getTNext(), epsilon = 1e-6;
        return channels.stream()
                .filter(channel ->
                        channel.getState() == 1 &&
                                Math.abs(channel.getTNext() - minTNext) < epsilon
                )
                .toList();
    }

    @Override
    public int getState() {
        return channels.stream()
                .allMatch(channel -> channel.getState() == 0)
                ? 0 : 1;
    }

    @Override
    public void doStatistics(double delta) {
        super.addMeanQueue(getQueue() * delta);
        super.addMeanLoad(getState() * delta);
    }
}
