package SM_CW_3_JAVA.P3;

import SM_CW_3_JAVA.P1.simsimple.Create;
import SM_CW_3_JAVA.P1.simsimple.ITask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CreateSick extends Create {
    private HashMap<Integer, Double> sickTypedFrequencies;

    public CreateSick(String name, double delay) {
        super(name, delay);
    }

    public void setSickTypedFrequencies(int @NotNull [] types, double[] frequencies) {
        this.sickTypedFrequencies = new HashMap<>();
        for (int i = 0; i < types.length; ++i) {
            this.sickTypedFrequencies.put(types[i], frequencies[i]);
        }
    }

    @Override
    protected ITask createTask() {
        return new Sick(super.getTCurr(), chooseSickType());
    }

    private int chooseSickType() {
        double random = Math.random();
        double sum = 0;
        for (int type : sickTypedFrequencies.keySet()) {
            sum += sickTypedFrequencies.get(type);
            if (random <= sum) {
                return type;
            }
        }
        return -1;
    }
}
