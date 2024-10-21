package SM_CW_2_JAVA.P1.simsimple;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Element implements IElement, Comparable<IElement> {
    private static int nextId = 0;
    private String name;
    private double tNext;
    private double delayMean, delayDev;
    private String distribution;
    private int quantity;
    private double tCurr;
    private int state;
    private IElement nextElement;
    private int id;

    public Element() {
        tNext = Double.MAX_VALUE;
        delayMean = 1.;
        distribution = "exp";
        tCurr = tNext;
        state = 0;
        nextElement = null;
        id = nextId;
        ++nextId;
        name = "element" + id;
    }

    public Element(double delay) {
        tNext = .0;
        delayMean = delay;
        distribution = "";
        tCurr = tNext;
        state = 0;
        nextElement = null;
        id = nextId;
        ++nextId;
        name = "element" + id;
    }

    public Element(String nameOfElement, double delay) {
        tNext = .0;
        delayMean = delay;
        distribution = "exp";
        tCurr = tNext;
        state = 0;
        nextElement = null;
        id = nextId;
        ++nextId;
        name = nameOfElement;
    }

    @Override
    public double getDelay() {
        double delay = getDelayMean();
        if ("exp".equalsIgnoreCase(getDistribution())) {
            delay = FunRand.Exp(getDelayMean());
        } else {
            if ("norm".equalsIgnoreCase(getDistribution())) {
                delay = FunRand.Norm(getDelayMean(), getDelayDev());
            } else {
                if ("unif".equalsIgnoreCase(getDistribution())) {
                    delay = FunRand.Unif(getDelayMean(), getDelayDev());
                } else {
                    if ("".equalsIgnoreCase(getDistribution())) {
                        delay = getDelayMean();
                    }
                }
            }
        }
        return delay;
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
    public String getDistribution() {
        return distribution;
    }

    @Override
    public void setDistribution(String distribution) {
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
    public void setId(int id) {
        this.id = id;
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
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void doStatistics(double delta) {
    }

    @Override
    public int compareTo(@NotNull IElement o) {
        return (new Random().nextInt(3) - 1) * o.getState();
    }
}
