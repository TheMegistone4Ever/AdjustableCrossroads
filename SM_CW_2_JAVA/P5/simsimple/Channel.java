package SM_CW_2_JAVA.P5.simsimple;

public class Channel {
    private int state = 0;
    private double tNext = Double.MAX_VALUE;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public double getTNext() {
        return tNext;
    }

    public void setTNext(double tNext) {
        this.tNext = tNext;
    }
}
