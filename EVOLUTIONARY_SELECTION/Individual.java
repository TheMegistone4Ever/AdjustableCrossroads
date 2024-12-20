package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import java.util.Arrays;

import static LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION.TrafficLightOptimizer.*;
import static LibTest.TERM_PAPER.POM.AdjustableCrossroads.*;

/**
 * Представляє індивідуальне рішення (хромосому) в генетичному алгоритмі.
 * Кожен індивідуум інкапсулює часи фаз світлофора та його оцінку придатності.
 */
public class Individual {

    /**
     * Ймовірність мутації генів індивідуума та оцінка придатності.
     */
    protected final int[] phaseTimes;
    protected double fitness;

    /**
     * Створює індивідуума з заданими часами фаз та оцінює його придатність.
     *
     * @param phaseTimes Масив тривалостей фаз світлофора
     */
    public Individual(int[] phaseTimes) {
        this.phaseTimes = Arrays.copyOf(phaseTimes, phaseTimes.length);
        fitness = evaluateFitness();
    }

    /**
     * Оцінює придатність поточної конфігурації світлофора.
     * Нижча придатність вказує на кращу продуктивність (менше машин, що очікують).
     *
     * @return Оцінка придатності, що представляє метрику заторів руху
     */
    private double evaluateFitness() {
        return getIndividualMetric(goStats(phaseTimes, arrivalTimesInit, SIMULATION_TIME, ITERATIONS));
    }

    /**
     * Мутує часи фаз індивідуума з імовірнісною варіацією.
     * Мутація допомагає досліджувати простір рішень та запобігати передчасній конвергенції.
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
