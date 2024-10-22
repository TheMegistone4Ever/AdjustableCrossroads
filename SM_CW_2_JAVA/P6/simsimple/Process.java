package SM_CW_2_JAVA.P6.simsimple;

import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_2_JAVA.P5.simsimple.Channel;

import java.util.List;
import java.util.PriorityQueue;

public class Process extends SM_CW_2_JAVA.P5.simsimple.Process {
    private final PriorityQueue<IElement> nextElements = new PriorityQueue<>();

    public Process(String nameOfElement, double delay, int maxQueue) {
        super(nameOfElement, delay, maxQueue);
    }

    @Override
    public void outAct() {
        List<Channel> soonestChannels = super.getSoonestChannels();
        super.addQuantity(soonestChannels.size());

        if (getNextElement() != null) {
            getNextElement().inAct();
        }

        for (Channel channel : soonestChannels) {
            channel.setTNext(Double.MAX_VALUE);
            channel.setState(0);
            if (getQueue() > 0) {
                setQueue(getQueue() - 1);
                channel.setState(1);
                channel.setTNext(super.getTCurr() + super.getDelay());
            }
        }
    }

    @Override
    public IElement getNextElement() {
        if (nextElements.isEmpty()) {
            return null;
        }
        return nextElements.peek();
    }

    @Override
    public void setNextElement(IElement nextElement) {
        nextElements.add(nextElement);
    }
}
