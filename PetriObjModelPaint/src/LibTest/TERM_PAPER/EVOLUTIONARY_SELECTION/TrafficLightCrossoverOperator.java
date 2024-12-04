package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import java.util.Random;

/**
 * Crossover operator for traffic light phase times.
 * Combines phase times from two parent individuals.
 */
public class TrafficLightCrossoverOperator {
    private static final Random random = new Random();
    private final double crossoverRate;

    /**
     * Constructor for crossover operator.
     * @param crossoverRate Probability of crossover occurring
     */
    public TrafficLightCrossoverOperator(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    /**
     * Perform crossover between two parent individuals.
     * @param parent1 First parent
     * @param parent2 Second parent
     * @return Array of two offspring
     */
    public TrafficLightIndividual[] crossover(
            TrafficLightIndividual parent1,
            TrafficLightIndividual parent2
    ) {
        if (random.nextDouble() > crossoverRate) {
            return new TrafficLightIndividual[]{
                    new TrafficLightIndividual(parent1.getPhaseTimes()),
                    new TrafficLightIndividual(parent2.getPhaseTimes())
            };
        }

        // Uniform crossover for first and third phase times
        double[] child1Times = parent1.getPhaseTimes().clone();
        double[] child2Times = parent2.getPhaseTimes().clone();

        // Crossover first phase (index 0)
        if (random.nextBoolean()) {
            child1Times[0] = parent2.getPhaseTimes()[0];
            child2Times[0] = parent1.getPhaseTimes()[0];
        }

        // Crossover third phase (index 2)
        if (random.nextBoolean()) {
            child1Times[2] = parent2.getPhaseTimes()[2];
            child2Times[2] = parent1.getPhaseTimes()[2];
        }

        return new TrafficLightIndividual[]{
                new TrafficLightIndividual(child1Times),
                new TrafficLightIndividual(child2Times)
        };
    }
}
