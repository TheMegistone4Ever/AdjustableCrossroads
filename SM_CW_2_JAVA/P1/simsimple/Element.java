package SM_CW_2_JAVA.P1.simsimple;

public class Element implements  IElement {
    private String name;
    private double tnext;
    private double delayMean, delayDev;
    private String distribution;
    private int quantity;
    private double tcurr;
    private int state;
    private IElement nextElement;
    private static int nextId=0;
    private int id;

    public Element(){
        tnext = Double.MAX_VALUE;
        delayMean = 1.0;
        distribution = "exp";
        tcurr = tnext;
        state=0;
        nextElement=null;
        id = nextId;
        ++nextId;
        name = "element"+id;
    }
    public Element(double delay){
        tnext = 0.0;
        delayMean = delay;
        distribution = "";
        tcurr = tnext;
        state=0;
        nextElement=null;
        id = nextId;
        ++nextId;
        name = "element"+id;
    }
    public Element(String nameOfElement, double delay){
        tnext = 0.0;
        delayMean = delay;
        distribution = "exp";
        tcurr = tnext;
        state=0;
        nextElement=null;
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
                delay = FunRand.Norm(getDelayMean(),
                        getDelayDev());
            } else {
                if ("unif".equalsIgnoreCase(getDistribution())) {
                    delay = FunRand.Unif(getDelayMean(),
                            getDelayDev());
                } else {
                    if("".equalsIgnoreCase(getDistribution())){
                        delay = getDelayMean();}
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
    public double getTcurr() {
        return tcurr;
    }
    @Override
    public void setTcurr(double tcurr) {
        this.tcurr = tcurr;
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
    public void outAct(){
        incQuantity();
    }

    @Override
    public double getTnext() {
        return tnext;
    }
    @Override
    public void setTnext(double tnext) {
        this.tnext = tnext;
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
    public void printResult(){
        System.out.println(getName()+ " quantity = "+ quantity);
    }

    @Override
    public void printInfo(){
        System.out.println(getName()+ " state= " +state+
                " quantity = "+ quantity+
                " tnext= "+tnext);
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
    public void doStatistics(double delta){

    }
}
