package SM_CW_3_JAVA.P3;

import SM_CW_3_JAVA.P1.simsimple.Task;

public class Sick extends Task {
    private int type;

    public Sick(double timeIn, int type) {
        super(timeIn);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
