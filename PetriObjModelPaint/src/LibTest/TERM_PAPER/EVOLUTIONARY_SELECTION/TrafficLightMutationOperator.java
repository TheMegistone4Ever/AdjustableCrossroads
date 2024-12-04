package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import java.util.Random;

/**
 * Mutation operator for traffic light phase times.
 * Modifies first and third phase times with a defined probability.
 */
public class TrafficLightMutationOperator {
    private static final Random random = new Random();
    private final double mutationRate;
    private final double mutationDeviation;
    private final double minTime;
    private final double maxTime;

    /**
     * Constructor for mutation operator.
     * @param mutationRate Probability of mutation occurring
     * @param mutationDeviation Maximum deviation for mutation
     * @param minTime Minimum allowed phase time
     * @param maxTime Maximum allowed phase time
     */
    public TrafficLightMutationOperator(
            double mutationRate,
            double mutationDeviation,
            double minTime,
            double maxTime
    ) {
        this.mutationRate = mutationRate;
        this.mutationDeviation = mutationDeviation;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    /**
     * Mutate an individual's phase times.
     * @param individual Individual to mutate
     * @return Mutated individual
     */
    public TrafficLightIndividual mutate(TrafficLightIndividual individual) {
        double[] phaseTimes = individual.getPhaseTimes();

        // Mutate first phase (index 0)
        if (random.nextDouble() < mutationRate) {
            double mutation = (random.nextDouble() * 2 - 1) * mutationDeviation;
            phaseTimes[0] = Math.max(minTime, Math.min(maxTime, phaseTimes[0] + mutation));
        }

        // Mutate third phase (index 2)
        if (random.nextDouble() < mutationRate) {
            double mutation = (random.nextDouble() * 2 - 1) * mutationDeviation;
            phaseTimes[2] = Math.max(minTime, Math.min(maxTime, phaseTimes[2] + mutation));
        }

        return new TrafficLightIndividual(phaseTimes);
    }
}
