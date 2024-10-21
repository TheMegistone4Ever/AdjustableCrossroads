package SM_CW_2_JAVA.P5.simsimple;

import SM_CW_2_JAVA.P1.simsimple.Element;
import SM_CW_2_JAVA.P1.simsimple.IElement;

import java.util.ArrayList;
import java.util.List;

public class Process extends SM_CW_2_JAVA.P2.simsimple.Process {
    private final ArrayList<Channel> channels = new ArrayList<>();

    public Process(double delay) {
        super(delay);
        channels.add(new Channel());
    }

    public Process(double delay, int channels) {
        super(delay);
        for (int i = 0; i < channels; ++i) {
            this.channels.add(new Channel());
        }
    }

    @Override
    public void inAct() {
        Channel freeChannel = getFreeChannel();
        if (freeChannel != null) {
            freeChannel.setState(1);
            freeChannel.setTnext(super.getTcurr() + super.getDelay());
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
            channel.setTnext(Double.MAX_VALUE);
            channel.setState(0);
            addState(-1);
            if (getQueue() > 0) {
                setQueue(getQueue() - 1);
                channel.setState(1);
                channel.setTnext(super.getTcurr() + super.getDelay());
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
    public double getTnext() {
        return channels.stream()
                .map(Channel::getTnext)
                .min(Double::compareTo)
                .orElse(Double.MAX_VALUE);
    }

    protected List<Channel> getSoonestChannels() {
        var minTnext = getTnext();
        return channels.stream()
                .filter(channel -> channel.getTnext() == minTnext && channel.getState() == 1)
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
