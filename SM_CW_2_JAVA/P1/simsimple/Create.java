package SM_CW_2_JAVA.P1.simsimple;

public class Create extends Element {
    public Create(String nameOfElement, double delay) {
        super(nameOfElement, delay);
        super.setTNext(0); // імітація розпочнеться з події Create
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTNext(super.getTCurr() + super.getDelay());
        super.getNextElement().inAct();
    }
}
