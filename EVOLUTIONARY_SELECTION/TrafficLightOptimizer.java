package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static LibTest.TERM_PAPER.POM.AdjustableCrossroads.phaseTimesInit;

/**
 * Модифікований генетичний алгоритм для оптимізації фаз світлофора
 * <p>
 * Цей клас реалізує генетичний алгоритм для оптимізації тривалості фаз світлофора
 * з метою мінімізації максимальної середньої кількості автомобілів, що очікують на перехресті.
 * <p>
 * Ключові особливості:
 * - Турнірний відбір для вибору батьків
 * - Збереження елітності
 * - Стратегії мутації та схрещування
 * - Відстеження придатності
 *
 * @author Микита Кисельов
 * @version 2.0
 */
public class TrafficLightOptimizer {

    /**
     * Параметри генетичного алгоритму та константи.
     */
    public static final int MIN_PHASE_TIME = 10;
    public static final int MAX_PHASE_TIME = 90;
    protected static final int TOURNEY_SIZE = 5;
    protected static final double CROSSOVER_RATE = 0.75;
    protected static final double CROSSOVER_ALPHA = 0.5;
    protected static final double MUTATION_RATE = 0.15;
    protected static final int MUTATION_DEV = 4;
    protected static final double penalty = Double.MAX_VALUE;
    protected static final Random RANDOM = new Random();
    private static final int POPULATION_SIZE = 20;
    private static final int MAX_GENERATIONS = 1000;
    private static final String CSV_FILE_PATH = "fitness_data.csv";
    private static final String CSV_HEADER = "Generation,Individual,Fitness,1'st phase,3'rd phase\n";

    /**
     * Запускає оптимізацію генетичним алгоритмом та відображує прогрес придатності.
     */
    public static void main(String[] args) {
        Population population = new Population(POPULATION_SIZE, phaseTimesInit);

        try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
            csvWriter.append(CSV_HEADER);

            for (int generation = 0; generation < MAX_GENERATIONS; ++generation) {
                writeFitnessData(csvWriter, generation, population);
                population.evolve();

                if (generation % 10 == 9 || generation == 0) {
                    printBestIndividual(generation, population.getBestIndividual());
                }
            }
            System.out.println("Дані придатності збережено в: " + CSV_FILE_PATH);
        } catch (IOException e) {
            System.err.println("[ПОМИЛКА] Помилка запису до CSV-файлу: " + e.getMessage());
        }

        printOptimizationResults(population.getBestIndividual());
    }

    /**
     * Записує дані придатності популяції в CSV-файл.
     */
    private static void writeFitnessData(BufferedWriter csvWriter, int generation, @NotNull Population population) throws IOException {
        for (int i = 0; i < population.individuals.length; ++i) {
            csvWriter.append(String.valueOf(generation));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(i));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(population.individuals[i].fitness));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(population.individuals[i].phaseTimes[0]));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(population.individuals[i].phaseTimes[2]));
            csvWriter.append("\n");
        }
    }

    /**
     * Виводить найкращу особу та її придатність для поточного покоління.
     */
    private static void printBestIndividual(int generation, @NotNull Individual best) {
        System.out.printf("Покоління %d: Найкраща придатність = %.4f%n", generation + 1, best.fitness);
        System.out.println("Тривалості фаз: " + Arrays.toString(best.phaseTimes));
    }

    /**
     * Виводить результати оптимізації.
     */
    private static void printOptimizationResults(@NotNull Individual best) {
        System.out.printf("\n--- Результати оптимізації ---%nНайкращі тривалості фаз: %d, %d, %d, %d%n",
                best.phaseTimes[0], best.phaseTimes[1],
                best.phaseTimes[2], best.phaseTimes[3]);
        System.out.printf("Найкраща придатність (Макс. очікуючих автомобілів): %.4f%n", best.fitness);
    }
}
