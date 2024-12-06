package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static LibTest.TERM_PAPER.POM.AdjustableCrossroads.phaseTimesInit;

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

    /**
     * Genetic algorithm parameters and constants.
     */
    static final int POPULATION_SIZE = 100;
    static final int MAX_GENERATIONS = 1000;
    static final double MUTATION_RATE = 0.15;
    static final double CROSSOVER_RATE = 0.75;
    static final double CROSSOVER_ALPHA = 0.5;
    static final double penalty = Double.MAX_VALUE;
    static final int MUTATION_DEV = 4;
    public static final int MIN_PHASE_TIME = 10;
    public static final int MAX_PHASE_TIME = 90;
    static final String CSV_FILE_PATH = "fitness_data.csv";
    static final String csvHeader = "Generation,Individual,Fitness\n";

    static final Random RANDOM = new Random();

    /**
     * Runs the genetic algorithm optimization and visualizes fitness progression.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        Population population = new Population(POPULATION_SIZE, phaseTimesInit);

        try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
            csvWriter.append(csvHeader);

            for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
                writeFitnessData(csvWriter, generation, population);
                population.evolve();

                if (generation % 10 == 9 || generation == 0) {
                    printBestIndividual(generation, population.getBestIndividual());
                }
            }

            System.out.println("Fitness data saved to: " + CSV_FILE_PATH);

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }

        printOptimizationResults(population.getBestIndividual());
    }

    private static void writeFitnessData(BufferedWriter csvWriter, int generation, @NotNull Population population) throws IOException {
        for (int i = 0; i < population.individuals.length; ++i) {
            csvWriter.append(String.valueOf(generation));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(i));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(population.individuals[i].fitness));
            csvWriter.append("\n");
        }
    }

    private static void printBestIndividual(int generation, @NotNull Individual best) {
        System.out.printf("Generation %d: Best Fitness = %.4f%n",
                generation + 1, best.fitness);
        System.out.println("Phase Times: " + Arrays.toString(best.phaseTimes));
    }

    private static void printOptimizationResults(@NotNull Individual best) {
        System.out.println("\n--- Optimization Results ---");
        System.out.printf("Best Phase Times: %d, %d, %d, %d%n",
                best.phaseTimes[0], best.phaseTimes[1],
                best.phaseTimes[2], best.phaseTimes[3]);
        System.out.printf("Best Fitness (Max Waiting Cars): %.4f%n", best.fitness);
    }
}
