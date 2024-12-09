package LibTest.TERM_PAPER.POM;

import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriObjModel;
import PetriObj.PetriSim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static LibTest.TERM_PAPER.POM.AdjustableCrossroads.*;

public class AdjustableCrossroadsCSVExport {

    private static final String CSV_HEADER = "ITERATION;TIME;MEAN_QUEUE_1;MEAN_QUEUE_2;MEAN_QUEUE_3;MEAN_QUEUE_4;MARK_1;MARK_2;MARK_3;MARK_4\n";

    /**
     * Запис статистики симуляції до CSV файлу з деталізацією по часу та ітераціям.
     *
     * @param phaseTimes     Масив часів фаз світлофора
     * @param arrivalTimes   Масив часів надходження автомобілів
     * @param simulationTime Загальний час симуляції
     * @param iterations     Кількість ітерацій
     * @param timeStep       Крок запису статистики (в мілісекундах)
     * @param csvFilePath    Шлях до CSV файлу
     */
    public static void exportSimulationToCSV(
            int[] phaseTimes,
            double[] arrivalTimes,
            double simulationTime,
            int iterations,
            int timeStep,
            String csvFilePath
    ) {
        try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter(csvFilePath))) {
            csvWriter.write(CSV_HEADER);

            IntStream.range(0, iterations).forEach(iteration -> {
                try {
                    for (double time = 0; time <= simulationTime; time += timeStep) {
                        ArrayList<PetriSim> connectedSimulationModels = createSimulationModels(phaseTimes, arrivalTimes);
                        connectTrafficSubsystems(connectedSimulationModels);
                        PetriObjModel model = new PetriObjModel(connectedSimulationModels);
                        model.setIsProtokol(false);
                        model.go(time);
                        double[] timeStats = getStatistics(model);

                        String csvRow = String.format(
                                "%d;%.2f;%.4f;%.4f;%.4f;%.4f;%.4f;%.4f;%.4f;%.4f\n",
                                iteration + 1, time,
                                timeStats[0], timeStats[1], timeStats[2], timeStats[3],
                                timeStats[4], timeStats[5], timeStats[6], timeStats[7]
                        );
                        synchronized (csvWriter) {
                            csvWriter.write(csvRow);
                        }
                    }
                } catch (ExceptionInvalidTimeDelay | IOException e) {
                    System.err.printf("[ПОМИЛКА] Помилка симуляції або запису: %s%n", e.getMessage());
                }
            });
            System.out.println("Дані симуляції збережено в: " + csvFilePath);
        } catch (IOException e) {
            System.err.println("[ПОМИЛКА] Помилка створення CSV-файлу: " + e.getMessage());
        }
    }

    /**
     * Головний метод для демонстрації використання.
     */
    public static void main(String[] args) {
        exportSimulationToCSV(
                phaseTimesInit,
                arrivalTimesInit,
                SIMULATION_TIME,
                ITERATIONS,
                100,
                "crossroads_simulation_data.csv"
        );
    }
}
