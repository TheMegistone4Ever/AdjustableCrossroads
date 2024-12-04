package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import PetriObj.ExceptionInvalidNetStructure;
import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriObjModel;
import LibTest.TERM_PAPER.POM.AdjustableCrossroads;

import java.util.Random;

/**
 * Represents an individual in the genetic algorithm population.
 * Each individual is a configuration of traffic light phase times.
 */
public class TrafficLightIndividual implements Comparable<TrafficLightIndividual> {
    private static final Random random = new Random();

    private final double[] phaseTimes;
    private final double fitness;

    /**
     * Constructor to create an individual with random phase times.
     * @param minTime Minimum allowed time for a phase
     * @param maxTime Maximum allowed time for a phase
     */
    public TrafficLightIndividual(double minTime, double maxTime) {
        this.phaseTimes = new double[4];
        // Keep other phase times constant, only mutate phases 0 and 2
        this.phaseTimes[0] = minTime + random.nextDouble() * (maxTime - minTime);  // First phase
        this.phaseTimes[1] = 10.0;  // Keep constant
        this.phaseTimes[2] = minTime + random.nextDouble() * (maxTime - minTime);  // Third phase
        this.phaseTimes[3] = 10.0;  // Keep constant

        this.fitness = evaluateFitness();
    }

    /**
     * Constructor to create an individual with specific phase times.
     */
    public TrafficLightIndividual(double[] phaseTimes) {
        this.phaseTimes = phaseTimes.clone();
        this.fitness = evaluateFitness();
    }

    /**
     * Evaluate fitness by running the Petri Net simulation.
     * @return Fitness value (lower is better)
     */
    private double evaluateFitness() {
        try {
            double[] arrivalTimes = {15.0, 9.0, 20.0, 35.0};
            PetriObjModel model = new PetriObjModel(AdjustableCrossroads.createSimulationModels(phaseTimes, arrivalTimes));
            model.setIsProtokol(false);
            model.go(AdjustableCrossroads.SIMULATION_TIME);

            return AdjustableCrossroads.getIndividualMetric(model);
        } catch (ExceptionInvalidTimeDelay e) {
            System.out.printf("Error: %s%n", e.getMessage());
            return Double.MAX_VALUE;
        }
    }

    public double[] getPhaseTimes() {
        return phaseTimes.clone();
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public int compareTo(TrafficLightIndividual other) {
        return Double.compare(this.fitness, other.fitness);
    }

    @Override
    public String toString() {
        return String.format("Phase Times: [%.2f, %.2f, %.2f, %.2f], Fitness: %.4f",
                phaseTimes[0], phaseTimes[1], phaseTimes[2], phaseTimes[3], fitness);
    }
}
