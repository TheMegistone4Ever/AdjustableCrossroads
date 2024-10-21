package SM_CW_2_JAVA.P6.simsimple;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_2_JAVA.P5.simsimple.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Process extends SM_CW_2_JAVA.P5.simsimple.Process {
    private final ArrayList<IElement> nextElements = new ArrayList<>();

    public Process(double delay) {
        super(delay);
    }

    public Process(double delay, int channels) {
        super(delay, channels);
    }

    @Override
    public void outAct() {
        List<Channel> soonestChannels = super.getSoonestChannels();
        super.addQuantity(soonestChannels.size());

        if (getNextElement() != null) {
            getNextElement().inAct();
        }

        for (Channel channel : soonestChannels) {
            channel.setTnext(Double.MAX_VALUE);
            channel.setState(0);
            if (getQueue() > 0) {
                setQueue(getQueue() - 1);
                channel.setState(1);
                channel.setTnext(super.getTcurr() + super.getDelay());
            }
        }
    }

    @Override
    public IElement getNextElement() {
        if (nextElements.isEmpty()) {
            return null;
        }
        return nextElements.get((int) (Math.random() * nextElements.size()));
    }

    @Override
    public void setNextElement(IElement nextElement) {
        nextElements.add(nextElement);
    }
}
