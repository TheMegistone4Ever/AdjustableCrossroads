package SM_CW_3_JAVA.P2;

import SM_CW_2_JAVA.P1.simsimple.Distribution;
import SM_CW_2_JAVA.P1.simsimple.IElement;
import SM_CW_3_JAVA.P1.simsimple.*;

import java.util.ArrayList;

public class SimModel {
    public static void main(String[] args) {
        Create creator = new Create("CREATOR", 0.5, Forking.PRIORITIZED); // 0.5 - mean time between arrivals
        Teller teller1 = new Teller("TELLER_1", 0.3, 3, 1); // 3 - max queue length, 1 - number of channels
        Teller teller2 = new Teller("TELLER_2", 0.3, 3, 1);

        // Initial conditions
        teller1.setDistribution(Distribution.NORMAL);
        teller1.setDelayMean(1.);
        teller1.setDelayDev(0.3);
        teller1.setState(1);
        teller1.addTasksToQueue(new Task(), new Task());

        teller2.setDistribution(Distribution.NORMAL);
        teller2.setDelayMean(1.);
        teller2.setDelayDev(0.3);
        teller2.setState(1);
        teller2.addTasksToQueue(new Task(), new Task());

        creator.setTNext(0.1); // First arrival

        // Define paths with priorities and probabilities
        creator.addPaths(new Path(teller1, 4, task -> teller1.getQueue().size() > teller2.getQueue().size() + 1), // Choose teller1 if shorter queue or no queue
                new Path(teller2, 3, task -> teller2.getQueue().size() > teller1.getQueue().size() + 1), // Choose teller2 if shorter queue
                new Path(teller1, 2, task -> teller1.getQueue().size() <= 3 && teller1.getQueue().size() + teller1.getState() + teller2.getQueue().size() + teller2.getState() < 8), // Default to teller1 if space available
                new Path(teller2, 1, task -> teller2.getQueue().size() <= 3 && teller1.getQueue().size() + teller1.getState() + teller2.getQueue().size() + teller2.getState() < 8)  // Default to teller2 if space available
        );

        ArrayList<IElement> list = new ArrayList<>();
        list.add(creator);
        list.add(teller1);
        list.add(teller2);

        Model model = new Model(list, false);
        model.simulate(1000.);
        model.printResult();

        // Calculate and print additional metrics
        double avgTeller1Load = teller1.getAccumulatedLoad() / model.getCurrentTime();
        double avgTeller2Load = teller2.getAccumulatedLoad() / model.getCurrentTime();
        double avgClientsInBank = (teller1.getAccumulatedQueue() + teller2.getAccumulatedQueue() + teller1.getAccumulatedLoad() + teller2.getAccumulatedLoad()) / model.getCurrentTime(); // Little's Law
        double avgTimeBetweenDepartures = model.getCurrentTime() / (teller1.getQuantity() + teller2.getQuantity());
        double avgTimeInBank = (teller1.getAccumulatedProcessingTime() + teller2.getAccumulatedProcessingTime()) / (teller1.getQuantity() + teller2.getQuantity());
        double avgQueue1Length = teller1.getAccumulatedQueue() / model.getCurrentTime();
        double avgQueue2Length = teller2.getAccumulatedQueue() / model.getCurrentTime();
        double refusalPercentage = (teller1.getFailures() + teller2.getFailures()) / (double) creator.getQuantity() * 100;
        double laneChanges = teller1.getLaneChanges() + teller2.getLaneChanges(); // Assuming laneChanges are tracked in Teller class

        System.out.println("\n-------------ADDITIONAL METRICS-------------");
        System.out.printf("1. Average Teller 1 Load: %.6f\n", avgTeller1Load);
        System.out.printf("   Average Teller 2 Load: %.6f\n", avgTeller2Load);
        System.out.printf("2. Average Clients in Bank: %.6f\n", avgClientsInBank);
        System.out.printf("3. Average Time Between Departures: %.6f\n", avgTimeBetweenDepartures);
        System.out.printf("4. Average Time in Bank: %.6f\n", avgTimeInBank);
        System.out.printf("5. Average Queue 1 Length: %.6f\n", avgQueue1Length);
        System.out.printf("   Average Queue 2 Length: %.6f\n", avgQueue2Length);
        System.out.printf("6. Refusal Percentage: %.6f%%\n", refusalPercentage);
        System.out.printf("7. Lane Changes: %.0f\n", laneChanges);
    }
}
