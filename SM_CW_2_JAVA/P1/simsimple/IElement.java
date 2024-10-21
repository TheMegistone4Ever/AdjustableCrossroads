package SM_CW_2_JAVA.P1.simsimple;

public interface IElement {
    double getDelay();
    double getDelayDev();
    void setDelayDev(double delayDev);
    String getDistribution();
    void setDistribution(String distribution);
    int getQuantity();
    void incQuantity();
    void addQuantity(int quantityToAdd);
    double getTcurr();
    void setTcurr(double tcurr);
    int getState();
    void setState(int state);
    IElement getNextElement();
    void setNextElement(IElement nextElement);
    void inAct();
    void outAct();
    double getTnext();
    void setTnext(double tnext);
    double getDelayMean();
    void setDelayMean(double delayMean);
    int getId();
    void setId(int id);
    void printResult();
    void printInfo();
    String getName();
    void setName(String name);
    void doStatistics(double delta);
}
