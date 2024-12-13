package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;

import static LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION.TrafficLightOptimizer.*;

/**
 * Керує популяцією осіб протягом еволюції генетичного алгоритму.
 */
public class Population {

    /**
     * Масив осіб, що складають популяцію.
     */
    protected Individual[] individuals;

    /**
     * Створює популяцію з варіаціями початкових часів фаз.
     *
     * @param size              Кількість осіб у популяції
     * @param initialPhaseTimes Базові часи фаз для початкової популяції
     */
    public Population(int size, int[] initialPhaseTimes) {
        individuals = new Individual[size];
        for (int i = 0; i < size; ++i) {
            int[] variedPhaseTimes = Arrays.copyOf(initialPhaseTimes, initialPhaseTimes.length);
            variedPhaseTimes[0] += RANDOM.nextInt(MAX_PHASE_TIME - MIN_PHASE_TIME + 1) + MIN_PHASE_TIME;
            variedPhaseTimes[2] += RANDOM.nextInt(MAX_PHASE_TIME - MIN_PHASE_TIME + 1) + MIN_PHASE_TIME;
            individuals[i] = new Individual(variedPhaseTimes);
        }
    }

    /**
     * Еволюція популяції: сортування, елітизм, відтворення та мутація.
     */
    public void evolve() {
        Arrays.sort(individuals, Comparator.comparingDouble(ind -> ind.fitness));

        Individual[] newGeneration = new Individual[individuals.length];

        // Елітизм: збереження кращих виконавців
        int eliteCount = individuals.length / 5;
        System.arraycopy(individuals, 0, newGeneration, 0, eliteCount);

        // Заповнення решти популяції через відтворення
        for (int i = eliteCount; i < newGeneration.length; ++i) {
            newGeneration[i] = (RANDOM.nextDouble() < CROSSOVER_RATE) ? createChild() : createMutatedIndividual();
        }

        individuals = newGeneration;
    }

    /**
     * Створює дочірню особу через турнірний відбір, схрещування та мутацію.
     *
     * @return Дочірня особа
     */
    private @NotNull Individual createChild() {
        Individual parent1 = tournamentSelection();
        Individual parent2 = tournamentSelection();

        // Схрещування та мутація
        int[] childPhaseTimes = crossover(parent1.phaseTimes, parent2.phaseTimes);
        Individual child = new Individual(childPhaseTimes);
        child.mutate();

        return child;
    }

    /**
     * Створює мутовану особу з випадково вибраної особи у популяції.
     *
     * @return Мутована особа
     */
    private @NotNull Individual createMutatedIndividual() {
        Individual mutatedIndividual = new Individual(
                individuals[RANDOM.nextInt(individuals.length)].phaseTimes
        );
        mutatedIndividual.mutate();
        return mutatedIndividual;
    }

    /**
     * Метод турнірного відбору для вибору батьківських осіб.
     *
     * @return Найкраща особа з випадкової підмножини турніру
     */
    private Individual tournamentSelection() {
        Individual best = individuals[RANDOM.nextInt(individuals.length)];
        for (int i = 1; i < TOURNEY_SIZE; ++i) {
            Individual candidate = individuals[RANDOM.nextInt(individuals.length)];
            if (candidate.fitness < best.fitness) {
                best = candidate;
            }
        }
        return best;
    }

    /**
     * Виконує рівномірне схрещування між двома батьківськими особами.
     *
     * @param parent1 Часи фаз першого батька
     * @param parent2 Часи фаз другого батька
     * @return Часи фаз дочірньої особи, згенеровані через схрещування
     */
    private int @NotNull [] crossover(int[] parent1, int[] parent2) {
        int[] child = Arrays.copyOf(parent1, parent1.length);
        for (int i = 0; i < child.length; ++i) {
            if (RANDOM.nextDouble() < CROSSOVER_ALPHA) {
                child[i] = parent2[i];
            }
        }
        return child;
    }

    /**
     * Отримує найкращу особу в поточній популяції.
     *
     * @return Особа з найнижчою придатністю (найкраще рішення)
     */
    public Individual getBestIndividual() {
        return Arrays.stream(individuals)
                .parallel()
                .min(Comparator.comparingDouble(ind -> ind.fitness))
                .orElse(null);
    }
}
