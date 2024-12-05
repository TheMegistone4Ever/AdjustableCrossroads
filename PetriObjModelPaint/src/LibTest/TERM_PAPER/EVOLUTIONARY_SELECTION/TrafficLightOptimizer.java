package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import PetriObj.ExceptionInvalidTimeDelay;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static LibTest.TERM_PAPER.POM.AdjustableCrossroads.*;

/**
 * Advanced Genetic Algorithm for Traffic Light Phase Optimization
 * <p>
 * This class implements a genetic algorithm to optimize traffic light phase durations
 * with the goal of minimizing the maximum average number of waiting cars at an intersection.
 * <p>
 * Key Features:
 * - Tournament selection for parent selection
 * - Elitism preservation
 * - Mutation and crossover strategies
 * - Fitness tracking
 *
 * @author Mykyta Kyselov
 * @version 2.0
 */
public class TrafficLightOptimizer {
    // Genetic Algorithm Configuration
    private static final int POPULATION_SIZE = 100;  // Increased for more diversity
    private static final int MAX_GENERATIONS = 1000; // Extended for more thorough exploration
    private static final double MUTATION_RATE = 0.15;
    private static final double CROSSOVER_RATE = 0.75;
    private static final int MUTATION_DEVIATION = 6; // Slightly increased mutation range
    private static final int MIN_PHASE_TIME = 10;
    private static final int MAX_PHASE_TIME = 90;
    private static final String CSV_FILE_PATH = "fitness_data.csv";

    private static final Random RANDOM = new Random();

    /**
     * Runs the genetic algorithm optimization and visualizes fitness progression.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Initial phase times [First direction, Second direction, Third direction, Fourth direction]
//        double[] initialPhaseTimes = {20.0, 10.0, 30.0, 10.0};

        Population population = new Population(POPULATION_SIZE, phaseTimesInit);

        // Track fitness progression for visualization
        try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
            // Write header row
            csvWriter.append("Generation,Individual,Fitness\n");

            // Evolution process
            for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
                // Store fitness values for current generation
                for (int i = 0; i < population.individuals.length; ++i) {
                    csvWriter.append(String.valueOf(generation));
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(i));
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(population.individuals[i].fitness));
                    csvWriter.append("\n");
                }
                population.evolve();

                // Print progress periodically
                Individual bestIndividual = population.getBestIndividual();
                if (generation % 10 == 9 || generation == 0) {
                    System.out.printf("Generation %d: Best Fitness = %.4f%n",
                            generation + 1, bestIndividual.fitness);
                    System.out.println("Phase Times: " + Arrays.toString(bestIndividual.phaseTimes));
                }
            }

            System.out.println("Fitness data saved to: " + CSV_FILE_PATH);

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }

        // Final results
        Individual bestSolution = population.getBestIndividual();
        System.out.println("\n--- Optimization Results ---");
        System.out.printf("Best Phase Times: %d, %d, %d, %d%n",
                bestSolution.phaseTimes[0], bestSolution.phaseTimes[1],
                bestSolution.phaseTimes[2], bestSolution.phaseTimes[3]);
        System.out.printf("Best Fitness (Max Waiting Cars): %.4f%n", bestSolution.fitness);
    }

    /**
     * Represents an individual solution (chromosome) in the genetic algorithm.
     * Each individual encapsulates traffic light phase times and its fitness score.
     */
    public static class Individual {
        int[] phaseTimes;
        double fitness;

        /**
         * Constructs an individual with given phase times and evaluates its fitness.
         *
         * @param phaseTimes Array of traffic light phase durations
         */
        public Individual(int[] phaseTimes) {
            this.phaseTimes = Arrays.copyOf(phaseTimes, phaseTimes.length);
            this.fitness = evaluateFitness();
        }

        /**
         * Evaluates the fitness of the current traffic light configuration.
         * Lower fitness indicates better performance (fewer waiting cars).
         *
         * @return Fitness score representing the traffic congestion metric
         */
        private double evaluateFitness() {
            try {
                // Predefined arrival times for different traffic streams
//                double[] arrivalTimes = {15.0, 9.0, 20.0, 35.0};
//                 Create and run Petri net simulation
//                ArrayList<PetriSim> connectedSimulationModels = createSimulationModels(phaseTimes, arrivalTimes);
//                connectTrafficSubsystems(connectedSimulationModels);
//
//                PetriObjModel model = new PetriObjModel(connectedSimulationModels);
//                model.setIsProtokol(false);
//                model.go(SIMULATION_TIME);
//
//                double[][] stats = goStats(phaseTimes, arrivalTimes, SIMULATION_TIME, ITERATIONS);
                return getIndividualMetric(goStats(phaseTimes, arrivalTimesInit, SIMULATION_TIME, ITERATIONS));
            } catch (ExceptionInvalidTimeDelay e) {
                System.err.printf("[ERROR] Invalid time delay: %s%n", e.getMessage());
                return Double.MAX_VALUE; // Penalize invalid configurations
            }
        }

        /**
         * Mutates the individual's phase times with probabilistic variation.
         * Mutation helps explore the solution space and prevent premature convergence.
         */
        public void mutate() {
            // Mutate first and third phase times with controlled randomness
            for (int i = 0; i < phaseTimes.length; i += 2) {
                if (RANDOM.nextDouble() < MUTATION_RATE) {
//                    phaseTimes[i] += (RANDOM.nextDouble() - 0.5) * 2 * MUTATION_DEVIATION;
//                    phaseTimes[i] = Math.max(10, Math.min(phaseTimes[i], 45)); // Constrain mutation range

                    // 50% change to increase, 50% to decrease
//                    int mutation = RANDOM.nextInt(MUTATION_DEVIATION * 2 + 1) - MUTATION_DEVIATION;
//                    phaseTimes[i] += MUTATION_DEVIATION * mutationSign;
                    phaseTimes[i] += RANDOM.nextInt(Math.abs(MUTATION_DEVIATION) * 2 + 1) - MUTATION_DEVIATION;;
                    phaseTimes[i] = Math.max(MIN_PHASE_TIME, Math.min(phaseTimes[i], MAX_PHASE_TIME));
                }
            }
            fitness = evaluateFitness(); // Re-evaluate after mutation
        }
    }

    /**
     * Manages the population of individuals throughout the genetic algorithm's evolution.
     */
    public static class Population {
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
                // Generate varied initial phase times
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
            // Sort individuals by fitness in ascending order
            Arrays.sort(individuals, Comparator.comparingDouble(ind -> ind.fitness));

            Individual[] newGeneration = new Individual[individuals.length];

            // Elitism: preserve top performers
            int eliteCount = individuals.length / 5;
            System.arraycopy(individuals, 0, newGeneration, 0, eliteCount);

            // Fill remaining population through reproduction
            for (int i = eliteCount; i < newGeneration.length; i++) {
                if (RANDOM.nextDouble() < CROSSOVER_RATE) {
                    Individual parent1 = tournamentSelection();
                    Individual parent2 = tournamentSelection();

                    // Crossover and mutation
                    int[] childPhaseTimes = crossover(parent1.phaseTimes, parent2.phaseTimes);
                    Individual child = new Individual(childPhaseTimes);
                    child.mutate();

                    newGeneration[i] = child;
                } else {
                    // Direct mutation or replication
                    Individual mutatedIndividual = new Individual(
                            individuals[RANDOM.nextInt(individuals.length)].phaseTimes
                    );
                    mutatedIndividual.mutate();
                    newGeneration[i] = mutatedIndividual;
                }
            }

            individuals = newGeneration;
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
        private int[] crossover(int[] parent1, int[] parent2) {
            int[] child = Arrays.copyOf(parent1, parent1.length);
            for (int i = 0; i < child.length; i++) {
                if (RANDOM.nextDouble() < 0.5) {
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
}
