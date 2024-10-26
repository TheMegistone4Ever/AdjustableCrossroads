package SM_CW_3_JAVA.P1.simsimple;

public class Create extends Element {
    public Create(String nameOfElement, double delay) {
        super(nameOfElement, delay);
        super.setTNext(.0); // імітація розпочнеться з події Create
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTNext(super.getTCurr() + super.getDelay());
        Task task = new Task();
        Path path = super.getNextPath(task);
        if (path.getTo() != null && !path.isBlocked(task)) {
            path.getTo().inAct(task);
        }
    }
}
