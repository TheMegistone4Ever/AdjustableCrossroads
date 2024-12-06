package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;

import static LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION.TrafficLightOptimizer.*;

/**
 * Manages the population of individuals throughout the genetic algorithm's evolution.
 */
public class Population {
    Individual[] individuals;

    /**
     * Creates a population with variations of initial phase times.
     *
     * @param size              Number of individuals in the population
     * @param initialPhaseTimes Base phase times for initial population
     */
    public Population(int size, int[] initialPhaseTimes) {
        individuals = new Individual[size];
        for (int i = 0; i < size; i++) {
            int[] variedPhaseTimes = Arrays.copyOf(initialPhaseTimes, initialPhaseTimes.length);
            variedPhaseTimes[0] += RANDOM.nextInt(MAX_PHASE_TIME - MIN_PHASE_TIME + 1) + MIN_PHASE_TIME;
            variedPhaseTimes[2] += RANDOM.nextInt(MAX_PHASE_TIME - MIN_PHASE_TIME + 1) + MIN_PHASE_TIME;
            individuals[i] = new Individual(variedPhaseTimes);
        }
    }

    /**
     * Evolves the population through selection, crossover, and mutation.
     */
    public void evolve() {
        Arrays.sort(individuals, Comparator.comparingDouble(ind -> ind.fitness));

        Individual[] newGeneration = new Individual[individuals.length];

        // Elitism: preserve top performers
        int eliteCount = individuals.length / 5;
        System.arraycopy(individuals, 0, newGeneration, 0, eliteCount);

        // Fill remaining population through reproduction
        for (int i = eliteCount; i < newGeneration.length; i++) {
            newGeneration[i] = (RANDOM.nextDouble() < CROSSOVER_RATE) ? createChild() : createMutatedIndividual();
        }

        individuals = newGeneration;
    }

    /**
     * Creates a child individual through tournament selection, crossover, and mutation.
     *
     * @return Child individual
     */
    private @NotNull Individual createChild() {
        Individual parent1 = tournamentSelection();
        Individual parent2 = tournamentSelection();

        // Crossover and mutation
        int[] childPhaseTimes = crossover(parent1.phaseTimes, parent2.phaseTimes);
        Individual child = new Individual(childPhaseTimes);
        child.mutate();

        return child;
    }

    /**
     * Creates a mutated individual from a randomly selected individual in the population.
     *
     * @return Mutated individual
     */
    private @NotNull Individual createMutatedIndividual() {
        Individual mutatedIndividual = new Individual(
                individuals[RANDOM.nextInt(individuals.length)].phaseTimes
        );
        mutatedIndividual.mutate();
        return mutatedIndividual;
    }

    /**
     * Tournament selection method for choosing parent individuals.
     *
     * @return Best individual from a random tournament subset
     */
    private Individual tournamentSelection() {
        int tournamentSize = 5;
        Individual best = individuals[RANDOM.nextInt(individuals.length)];
        for (int i = 1; i < tournamentSize; i++) {
            Individual candidate = individuals[RANDOM.nextInt(individuals.length)];
            if (candidate.fitness < best.fitness) {
                best = candidate;
            }
        }
        return best;
    }

    /**
     * Performs uniform crossover between two parent individuals.
     *
     * @param parent1 First parent's phase times
     * @param parent2 Second parent's phase times
     * @return Child's phase times generated through crossover
     */
    private int @NotNull [] crossover(int[] parent1, int[] parent2) {
        int[] child = Arrays.copyOf(parent1, parent1.length);
        for (int i = 0; i < child.length; i++) {
            if (RANDOM.nextDouble() < CROSSOVER_ALPHA) {
                child[i] = parent2[i];
            }
        }
        return child;
    }

    /**
     * Retrieves the best-performing individual in the current population.
     *
     * @return Individual with the lowest fitness ( the best solution)
     */
    public Individual getBestIndividual() {
        return Arrays.stream(individuals)
                .min(Comparator.comparingDouble(ind -> ind.fitness))
                .orElse(null);
    }
}
