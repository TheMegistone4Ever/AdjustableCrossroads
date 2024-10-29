package SM_CW_3_JAVA.P1.simsimple;

import SM_CW_2_JAVA.P1.simsimple.IElement;

public class Create extends Element {
    public Create(String nameOfElement, double delay) {
        super(nameOfElement, delay);
        super.setTNext(.0); // імітація розпочнеться з події Create
    }

    public Create(String nameOfElement, double delay, Forking forking) {
        super(nameOfElement, delay, forking);
        super.setTNext(.0); // імітація розпочнеться з події Create
    }

    public Create(String nameOfElement, double delay, double initialTNext) {
        super(nameOfElement, delay);
        super.setTNext(initialTNext);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTNext(super.getTCurr() + super.getDelay());
        ITask task = createTask();
        Path path = super.getNextPath(task);
        IElement to = path.getTo();
        if (to != null && !path.isBlocked(task)) {
            to.inAct(task);
        }
    }

    protected Task createTask() {
        return new Task(super.getTCurr());
    }
}
