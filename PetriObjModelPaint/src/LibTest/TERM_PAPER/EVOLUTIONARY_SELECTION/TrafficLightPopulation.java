package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Manages the population for genetic algorithm optimization.
 */
public class TrafficLightPopulation {
    private static final Random random = new Random();

    private List<TrafficLightIndividual> individuals;
    private final TrafficLightMutationOperator mutationOperator;
    private final TrafficLightCrossoverOperator crossoverOperator;

    /**
     * Initialize population with random individuals.
     * @param populationSize Number of individuals
     * @param mutationOperator Mutation strategy
     * @param crossoverOperator Crossover strategy
     * @param minTime Minimum phase time
     * @param maxTime Maximum phase time
     */
    public TrafficLightPopulation(
            int populationSize,
            TrafficLightMutationOperator mutationOperator,
            TrafficLightCrossoverOperator crossoverOperator,
            double minTime,
            double maxTime
    ) {
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;

        this.individuals = IntStream.range(0, populationSize)
                .mapToObj(i -> new TrafficLightIndividual(minTime, maxTime))
                .collect(Collectors.toList());
    }

    /**
     * Select individuals using tournament selection.
     * @param tournamentSize Size of tournament
     * @param selectionCount Number of individuals to select
     * @return Selected individuals
     */
    public List<TrafficLightIndividual> tournamentSelection(
            int tournamentSize,
            int selectionCount
    ) {
        List<TrafficLightIndividual> selected = new ArrayList<>();

        for (int i = 0; i < selectionCount; i++) {
            List<TrafficLightIndividual> tournament = new ArrayList<>();
            for (int j = 0; j < tournamentSize; j++) {
                tournament.add(individuals.get(random.nextInt(individuals.size())));
            }

            selected.add(Collections.min(tournament));
        }

        return selected;
    }

    /**
     * Evolve population to next generation.
     */
    public void evolve() {
        // Select parents
        List<TrafficLightIndividual> parents = tournamentSelection(3, individuals.size() / 2);
        List<TrafficLightIndividual> nextGeneration = new ArrayList<>();

        // Crossover and mutation
        for (int i = 0; i < parents.size(); i += 2) {
            TrafficLightIndividual[] offspring = crossoverOperator.crossover(
                    parents.get(i),
                    parents.get(i + 1)
            );

            nextGeneration.add(mutationOperator.mutate(offspring[0]));
            nextGeneration.add(mutationOperator.mutate(offspring[1]));
        }

        this.individuals = nextGeneration;
    }

    /**
     * Get the best individual in the population.
     * @return Best individual based on fitness
     */
    public TrafficLightIndividual getBestIndividual() {
        return Collections.min(individuals);
    }

    public List<TrafficLightIndividual> getIndividuals() {
        return Collections.unmodifiableList(individuals);
    }
}
