package SM_CW_2_JAVA.P5.simsimple;

public class Channel {
    private int state = 0;
    private double tnext = Double.MAX_VALUE;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public double getTnext() {
        return tnext;
    }

    public void setTnext(double tnext) {
        this.tnext = tnext;
    }
}
