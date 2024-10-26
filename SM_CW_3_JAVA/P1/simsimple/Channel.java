package SM_CW_3_JAVA.P1.simsimple;

public class Channel {
    private Task task = null;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public double getTNext() {
        return task != null ? task.getTNext() : Double.MAX_VALUE;
    }

    public void setTNext(double tNext) {
        if (task != null) {
            task.setTNext(tNext);
        }
    }

    public boolean getState() {
        return task != null;
    }
}
