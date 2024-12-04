package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the genetic algorithm optimization for traffic light phases.
 */
public class TrafficLightGeneticAlgorithm {
    private static final int POPULATION_SIZE = 50;
    private static final int MAX_GENERATIONS = 100;
    private static final double MIN_PHASE_TIME = 10.0;
    private static final double MAX_PHASE_TIME = 50.0;
    private static final double MUTATION_RATE = 0.2;
    private static final double MUTATION_DEVIATION = 2.0;
    private static final double CROSSOVER_RATE = 0.7;

    public static void main(String[] args) {
        // Operators
        TrafficLightMutationOperator mutationOperator =
                new TrafficLightMutationOperator(
                        MUTATION_RATE,
                        MUTATION_DEVIATION,
                        MIN_PHASE_TIME,
                        MAX_PHASE_TIME
                );

        TrafficLightCrossoverOperator crossoverOperator =
                new TrafficLightCrossoverOperator(CROSSOVER_RATE);

        // Population
        TrafficLightPopulation population = new TrafficLightPopulation(
                POPULATION_SIZE,
                mutationOperator,
                crossoverOperator,
                MIN_PHASE_TIME,
                MAX_PHASE_TIME
        );

        // Store fitness progression
        List<Double> fitnessProgression = new ArrayList<>();

        // Evolve population
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            TrafficLightIndividual bestIndividual = population.getBestIndividual();
            fitnessProgression.add(bestIndividual.getFitness());

            System.out.printf("Generation %d: Best Individual - %s%n",
                    generation, bestIndividual);

            population.evolve();
        }

        // Final best individual
        TrafficLightIndividual bestSolution = population.getBestIndividual();
        System.out.println("\nOptimal Solution Found:");
        System.out.println(bestSolution);

        // Create fitness progression chart
        createFitnessChart(fitnessProgression);
    }

    /**
     * Create a visualization of fitness progression.
     *
     * @param fitnessValues List of fitness values across generations
     */
    private static void createFitnessChart(List<Double> fitnessValues) {
        XYSeries series = new XYSeries("Fitness Progression");
        for (int i = 0; i < fitnessValues.size(); i++) {
            series.add(i, fitnessValues.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Traffic Light Optimization - Fitness Progression",
                "Generation",
                "Fitness Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        JFrame frame = new JFrame("Genetic Algorithm - Fitness Progression");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
