package SM_CW_2_JAVA.P1.simsimple;

import SM_CW_3_JAVA.P1.simsimple.ITask;
import SM_CW_3_JAVA.P1.simsimple.Path;

public interface IElement {
    double getDelay();

    double getDelayDev();

    void setDelayDev(double delayDev);

    void setDistribution(Distribution distribution);

    int getQuantity();

    void incQuantity();

    void addQuantity(int quantityToAdd);

    double getTCurr();

    void setTCurr(double tCurr);

    int getState();

    void setState(int state);

    void addState(int stateToAdd);

    IElement getNextElement();

    void setNextElement(IElement nextElement);

    void inAct();

    void inAct(ITask task);

    void outAct();

    double getTNext();

    void setTNext(double tNext);

    double getDelayMean();

    void setDelayMean(double delayMean);

    int getId();

    void printResult();

    void printInfo();

    String getName();

    void doStatistics(double delta);

    Model getParentModel();

    void setParentModel(Model parentModel);

    void addPaths(Path... paths);
}
