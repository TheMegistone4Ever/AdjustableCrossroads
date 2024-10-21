package SM_CW_2_JAVA.P6.simsimple;

import SM_CW_2_JAVA.P1.simsimple.IElement;

import java.util.ArrayList;
import java.util.Collection;

public class Process extends SM_CW_2_JAVA.P2.simsimple.Process {
    private final ArrayList<IElement> nextElements = new ArrayList<>();

    public Process(double delay) {
        super(delay);
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
