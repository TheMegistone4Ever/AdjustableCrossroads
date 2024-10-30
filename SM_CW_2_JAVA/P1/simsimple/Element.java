package SM_CW_2_JAVA.P1.simsimple;

import SM_CW_3_JAVA.P1.simsimple.ITask;
import org.jetbrains.annotations.NotNull;

import static SM_CW_2_JAVA.P1.simsimple.FunRand.*;

public class Element implements IElement, Comparable<IElement> {
    private static int nextId = -1;
    private final String name;
    private final int id = ++nextId;
    private double tNext = .0;
    private double delayMean, delayDev;
    private Distribution distribution = Distribution.EXPONENTIAL;
    private int quantity;
    private double tCurr = .0;
    private int state = 0;
    private IElement nextElement = null;
    private Model parentModel;

    public Element(String nameOfElement) {
        name = nameOfElement;
    }

    public Element(String nameOfElement, double delay) {
        delayMean = delay;
        name = nameOfElement;
    }

    public Element(String name, double delayMean, double delayDev) {
        this.name = name;
        this.delayMean = delayMean;
        this.delayDev = delayDev;
        distribution = Distribution.NORMAL;
    }

    @Override
    public double getDelay() {
        return switch (distribution) {
            case EXPONENTIAL -> Exp(getDelayMean());
            case NORMAL -> Norm(getDelayMean(), getDelayDev());
            case UNIFORM -> Unif(getDelayMean(), getDelayDev());
            case ERLANG -> Erlang(getDelayMean(), getDelayDev());
        };
    }

    @Override
    public double getDelayDev() {
        return delayDev;
    }

    @Override
    public void setDelayDev(double delayDev) {
        this.delayDev = delayDev;
    }

    @Override
    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void incQuantity() {
        ++quantity;
    }

    @Override
    public void addQuantity(int quantityToAdd) {
        quantity += quantityToAdd;
    }

    @Override
    public double getTCurr() {
        return tCurr;
    }

    @Override
    public void setTCurr(double tCurr) {
        this.tCurr = tCurr;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void addState(int stateToAdd) {
        state += stateToAdd;
    }

    @Override
    public IElement getNextElement() {
        return nextElement;
    }

    @Override
    public void setNextElement(IElement nextElement) {
        this.nextElement = nextElement;
    }

    @Override
    public void inAct() {
    }

    @Override
    public void inAct(ITask task) {
    }

    @Override
    public void outAct() {
        incQuantity();
    }

    @Override
    public double getTNext() {
        return tNext;
    }

    @Override
    public void setTNext(double tNext) {
        this.tNext = tNext;
    }

    @Override
    public double getDelayMean() {
        return delayMean;
    }

    @Override
    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void printResult() {
        System.out.printf("\n%s quantity=%d\n", getName(), quantity);
    }

    @Override
    public void printInfo() {
        System.out.printf("%s\n\ttNext=%s\n\tstate=%d\n\tquantity=%d\n", getName(),
                getTNext() == Double.MAX_VALUE ? "+inf" : String.format("%.6f", getTNext()), state, quantity);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void doStatistics(double delta) {
    }

    @Override
    public Model getParentModel() {
        return parentModel;
    }

    @Override
    public void setParentModel(Model parentModel) {
        this.parentModel = parentModel;
    }

    @Override
    public int compareTo(@NotNull IElement o) {
        return o.getState();
    }
}
