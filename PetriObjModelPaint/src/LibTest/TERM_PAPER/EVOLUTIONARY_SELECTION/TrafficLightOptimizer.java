package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import LibTest.TERM_PAPER.POM.AdjustableCrossroads;
import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriObjModel;
import PetriObj.PetriSim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Genetic Algorithm for Traffic Light Phase Optimization
 * Aims to minimize the maximum average number of cars waiting at an intersection
 */
public class TrafficLightOptimizer {
    // Genetic Algorithm Parameters
    private static final int POPULATION_SIZE = 50;
    private static final int MAX_GENERATIONS = 100;
    private static final double MUTATION_RATE = 0.2;
    private static final double CROSSOVER_RATE = 0.7;
    private static final double MUTATION_DEVIATION = 1.0;

    private static final Random RANDOM = new Random();

    /**
     * Individual representing a traffic light phase configuration
     */
    public static class Individual {
        double[] phaseTimes;
        double fitness;

        public Individual(double[] phaseTimes) {
            this.phaseTimes = Arrays.copyOf(phaseTimes, phaseTimes.length);
            this.fitness = evaluateFitness();
        }

        private double evaluateFitness() {
            try {
                double[] arrivalTimes = {15.0, 9.0, 20.0, 35.0};
                ArrayList<PetriSim> simulationModels = AdjustableCrossroads.createSimulationModels(phaseTimes, arrivalTimes);
                AdjustableCrossroads.connectTrafficSubsystems(simulationModels);

                PetriObjModel model = new PetriObjModel(simulationModels);
                model.setIsProtokol(false);
                model.go(AdjustableCrossroads.SIMULATION_TIME);

                return AdjustableCrossroads.getIndividualMetric(model);
            } catch (ExceptionInvalidTimeDelay e) {
                e.printStackTrace();
                return Double.MAX_VALUE;
            }
        }

        public void mutate() {
            if (RANDOM.nextDouble() < MUTATION_RATE) {
                // Mutate first phase time (index 0)
                phaseTimes[0] += (RANDOM.nextDouble() - 0.5) * 2 * MUTATION_DEVIATION;
                phaseTimes[0] = Math.max(10, Math.min(phaseTimes[0], 40)); // Constraining mutation range
            }
            if (RANDOM.nextDouble() < MUTATION_RATE) {
                // Mutate third phase time (index 2)
                phaseTimes[2] += (RANDOM.nextDouble() - 0.5) * 2 * MUTATION_DEVIATION;
                phaseTimes[2] = Math.max(10, Math.min(phaseTimes[2], 40)); // Constraining mutation range
            }
            // Re-evaluate fitness after mutation
            fitness = evaluateFitness();
        }
    }

    /**
     * Population management class for genetic algorithm
     */
    public static class Population {
        Individual[] individuals;

        public Population(int size, double[] initialPhaseTimes) {
            individuals = new Individual[size];
            for (int i = 0; i < size; i++) {
                // Create variations of initial phase times
                double[] variedPhaseTimes = Arrays.copyOf(initialPhaseTimes, initialPhaseTimes.length);
                variedPhaseTimes[0] += (RANDOM.nextDouble() - 0.5) * 10;
                variedPhaseTimes[2] += (RANDOM.nextDouble() - 0.5) * 10;
                individuals[i] = new Individual(variedPhaseTimes);
            }
        }

        public void evolve() {
            // Sort individuals by fitness
            Arrays.sort(individuals, Comparator.comparingDouble(ind -> ind.fitness));

            // Selection and reproduction
            Individual[] newGeneration = new Individual[individuals.length];

            // Elitism: keep top 20% of individuals
            int eliteCount = individuals.length / 5;
            for (int i = 0; i < eliteCount; i++) {
                newGeneration[i] = individuals[i];
            }

            // Fill rest of the population through crossover and mutation
            for (int i = eliteCount; i < newGeneration.length; i++) {
                if (RANDOM.nextDouble() < CROSSOVER_RATE) {
                    // Tournament selection
                    Individual parent1 = tournamentSelection();
                    Individual parent2 = tournamentSelection();

                    // Crossover
                    double[] childPhaseTimes = crossover(parent1.phaseTimes, parent2.phaseTimes);
                    Individual child = new Individual(childPhaseTimes);
                    child.mutate();

                    newGeneration[i] = child;
                } else {
                    // Direct mutation or copy
                    Individual mutatedIndividual = new Individual(individuals[RANDOM.nextInt(individuals.length)].phaseTimes);
                    mutatedIndividual.mutate();
                    newGeneration[i] = mutatedIndividual;
                }
            }

            individuals = newGeneration;
        }

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

        private double[] crossover(double[] parent1, double[] parent2) {
            double[] child = Arrays.copyOf(parent1, parent1.length);
            // Uniform crossover
            for (int i = 0; i < child.length; i++) {
                if (RANDOM.nextDouble() < 0.5) {
                    child[i] = parent2[i];
                }
            }
            return child;
        }

        public Individual getBestIndividual() {
            return Arrays.stream(individuals)
                    .min(Comparator.comparingDouble(ind -> ind.fitness))
                    .orElse(null);
        }
    }

    /**
     * Main method to run the genetic algorithm optimization
     */
    public static void main(String[] args) {
        // Initial phase times from the original implementation
        double[] initialPhaseTimes = {20.0, 10.0, 30.0, 10.0};

        Population population = new Population(POPULATION_SIZE, initialPhaseTimes);

        // Tracking fitness progression
        double[] fitnessProgression = new double[MAX_GENERATIONS];

        // Evolution process
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            population.evolve();
            Individual bestIndividual = population.getBestIndividual();
            fitnessProgression[generation] = bestIndividual.fitness;

            // Print progress periodically
            if (generation % 10 == 0) {
                System.out.printf("Generation %d: Best Fitness = %.4f%n",
                        generation, bestIndividual.fitness);
                System.out.println("Phase Times: " +
                        Arrays.toString(bestIndividual.phaseTimes));
            }
        }

        // Final results
        Individual bestSolution = population.getBestIndividual();
        System.out.println("\n--- Optimization Results ---");
        System.out.printf("Best Phase Times: [%.2f, %n\t%n\t%n\t%.2f]%n",
                bestSolution.phaseTimes[0], bestSolution.phaseTimes[2]);
        System.out.printf("Best Fitness (Max Waiting Cars): %.4f%n",
                bestSolution.fitness);

        // Optional: Here you could add code to generate visualization
        // of fitness progression
    }
}
