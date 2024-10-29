package SM_CW_3_JAVA.P1.simsimple;

import java.util.ArrayList;

public class Dispose extends Element {
    ArrayList<ITask> processedTasks = new ArrayList<>();

    public Dispose(String name) {
        super(name);
    }

    @Override
    public void inAct(ITask task) {
        super.inAct(task);
        processedTasks.add(task);
        super.outAct();
    }

    @Override
    public void printInfo() {
        System.out.println(getName() + " quantity = " + getQuantity());
    }

    public ArrayList<ITask> getProcessedJobs() {
        return processedTasks;
    }
}
