package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import PetriObj.ExceptionInvalidTimeDelay;

import java.util.Arrays;

import static LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION.TrafficLightOptimizer.*;
import static LibTest.TERM_PAPER.POM.AdjustableCrossroads.*;

/**
 * Represents an individual solution (chromosome) in the genetic algorithm.
 * Each individual encapsulates traffic light phase times and its fitness score.
 */
public class Individual {
    protected final int[] phaseTimes;
    protected double fitness;

    /**
     * Constructs an individual with given phase times and evaluates its fitness.
     *
     * @param phaseTimes Array of traffic light phase durations
     */
    public Individual(int[] phaseTimes) {
        this.phaseTimes = Arrays.copyOf(phaseTimes, phaseTimes.length);
        fitness = evaluateFitness();
    }

    /**
     * Evaluates the fitness of the current traffic light configuration.
     * Lower fitness indicates better performance (fewer waiting cars).
     *
     * @return Fitness score representing the traffic congestion metric
     */
    private double evaluateFitness() {
        try {
            return getIndividualMetric(goStats(phaseTimes, arrivalTimesInit, SIMULATION_TIME, ITERATIONS));
        } catch (ExceptionInvalidTimeDelay e) {
            System.err.printf("[ERROR] Invalid time delay: %s%n", e.getMessage());
            return penalty;
        }
    }

    /**
     * Mutates the individual's phase times with probabilistic variation.
     * Mutation helps explore the solution space and prevent premature convergence.
     */
    public void mutate() {
        for (int i = 0; i < phaseTimes.length; i += 2) {
            if (RANDOM.nextDouble() < MUTATION_RATE) {
                phaseTimes[i] = Math.max(
                        MIN_PHASE_TIME,
                        Math.min(
                                phaseTimes[i] + RANDOM.nextInt(Math.abs(MUTATION_DEV) * 2 + 1) - MUTATION_DEV,
                                MAX_PHASE_TIME
                        )
                );
            }
        }
        fitness = evaluateFitness();
    }
}
