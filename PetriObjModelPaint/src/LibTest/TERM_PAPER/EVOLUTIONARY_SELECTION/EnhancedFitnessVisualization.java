package LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EnhancedFitnessVisualization {
    /**
     * Creates an enhanced visualization of fitness progression with box plots and line graphs.
     *
     * @param populationFitnesses 2D array of fitness values for each individual in each generation
     */
    public static void visualizeFitnessProgression(double[][] populationFitnesses) {
        // Prepare datasets
        DefaultBoxAndWhiskerXYDataset boxPlotDataset = new DefaultBoxAndWhiskerXYDataset("Fitness Distribution");
        XYSeriesCollection bestFitnessLineDataset = new XYSeriesCollection();
        XYSeries bestFitnessSeries = new XYSeries("Best Fitness");
        XYSeries avgFitnessSeries = new XYSeries("Average Fitness");

        // Process fitness data
        for (int generation = 0; generation < populationFitnesses.length; generation++) {
            double[] generationFitnesses = populationFitnesses[generation];

            // Calculate statistics for box plot
            List<Double> fitnessList = new ArrayList<>();
            for (double fitness : generationFitnesses) {
                fitnessList.add(fitness);
            }

            // Calculate percentiles for box plot
            double mean = Arrays.stream(generationFitnesses).average().orElse(0);
            double min = Arrays.stream(generationFitnesses).min().orElse(0);
            double max = Arrays.stream(generationFitnesses).max().orElse(0);
            double median = calculateMedian(generationFitnesses);
            double q1 = calculatePercentile(generationFitnesses, 25);
            double q3 = calculatePercentile(generationFitnesses, 75);
            double iqr = q3 - q1;
            // Create box and whisker item
            BoxAndWhiskerItem boxItem = new BoxAndWhiskerItem(
                    mean,
                    median,
                    q1,
                    q3,
                    min,
                    max,
                    q1 - 1.5 * iqr,
                    q3 + 1.5 * iqr,
                    fitnessList
            );

            // Add to datasets
            boxPlotDataset.add(new Date(generation), boxItem);

            // Track best and average fitness
            bestFitnessSeries.add(generation, min);
            avgFitnessSeries.add(generation, Arrays.stream(generationFitnesses).average().orElse(0));
        }

        // Best and average fitness line dataset
        bestFitnessLineDataset.addSeries(bestFitnessSeries);
        bestFitnessLineDataset.addSeries(avgFitnessSeries);

        // Create combined plot
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(new NumberAxis("Generation"));
        combinedPlot.setOrientation(PlotOrientation.VERTICAL);

        // Box plot
        NumberAxis boxPlotRangeAxis = new NumberAxis("Fitness Distribution");
        XYBoxAndWhiskerRenderer boxRenderer = new XYBoxAndWhiskerRenderer();
        boxRenderer.setFillBox(true);
        boxRenderer.setBoxWidth(0.5);
        XYPlot boxPlot = new XYPlot(boxPlotDataset, null, boxPlotRangeAxis, boxRenderer);

        // Line plot for best and average fitness
        NumberAxis lineRangeAxis = new NumberAxis("Best/Average Fitness");
        XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true, true);
        lineRenderer.setSeriesPaint(0, Color.RED);  // Best fitness line
        lineRenderer.setSeriesPaint(1, Color.BLUE); // Average fitness line
        XYPlot linePlot = new XYPlot(bestFitnessLineDataset, null, lineRangeAxis, lineRenderer);

        // Combine plots
        combinedPlot.add(boxPlot, 2);
        combinedPlot.add(linePlot, 1);

        // Create chart
        JFreeChart chart = new JFreeChart(
                "Genetic Algorithm - Fitness Progression",
                JFreeChart.DEFAULT_TITLE_FONT,
                combinedPlot,
                true
        );

        // Display chart
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1000, 800));

        JFrame frame = new JFrame("Fitness Progression Analysis");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Calculate median of an array of doubles.
     *
     * @param values Array of fitness values
     * @return Median value
     */
    private static double calculateMedian(double[] values) {
        Arrays.sort(values);
        int length = values.length;
        if (length % 2 == 0) {
            return (values[length / 2 - 1] + values[length / 2]) / 2.0;
        } else {
            return values[length / 2];
        }
    }

    /**
     * Calculate specific percentile of an array of doubles.
     *
     * @param values     Array of fitness values
     * @param percentile Percentile to calculate (e.g., 25 for first quartile)
     * @return Percentile value
     */
    private static double calculatePercentile(double[] values, double percentile) {
        Arrays.sort(values);
        int index = (int) Math.ceil((percentile / 100.0) * values.length) - 1;
        return values[Math.max(0, Math.min(index, values.length - 1))];
    }
}