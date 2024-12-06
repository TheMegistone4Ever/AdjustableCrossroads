package LibTest.TERM_PAPER.POM;

import PetriObj.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION.TrafficLightOptimizer.MAX_PHASE_TIME;
import static LibTest.TERM_PAPER.EVOLUTIONARY_SELECTION.TrafficLightOptimizer.MIN_PHASE_TIME;

/**
 * Клас для моделювання адаптивного перехрестя з використанням мереж Петрі.
 * Симулює рух транспорту з урахуванням світлофорного регулювання.
 */
public class AdjustableCrossroads {

    /**
     * Константи для симуляції руху на перехресті.
     */
    public static final double SIMULATION_TIME = 1_000;
    public static final int ITERATIONS = 2;
    public static final int[] phaseTimesInit = {20, 10, 30, 10};
    public static final double[] arrivalTimesInit = {15.0, 9.0, 20.0, 35.0};

    /**
     * Головний метод для запуску симуляції руху на перехресті.
     */
    public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        double[][] stats = goStats(phaseTimesInit, arrivalTimesInit, SIMULATION_TIME, ITERATIONS);

        printStatistics(stats);

        // Виведення максимальної середньої кількості очікування (метрика індивіда популяції)
        System.out.printf(String.format(
                "\nМаксимальна кількість автомобілів, що очікують переїзду перехрестя в середньому за %d ітерацій: %.4f%n",
                ITERATIONS,
                getIndividualMetric(stats)
        ));

        double minIndividualMetric = findOptimalPhaseTimes(false);
        System.out.printf("Мінімальна метрика індивіда популяції (найкраща ефективність): %.4f%n", minIndividualMetric);
    }

    /**
     * Пошук оптимальних часів фаз для перехрестя.
     */
    private static double findOptimalPhaseTimes(boolean isSearching) throws ExceptionInvalidTimeDelay {
        double minIndividualMetric = Double.MAX_VALUE;
        for (int phase1 = MIN_PHASE_TIME; phase1 <= MAX_PHASE_TIME; ++phase1) {
            for (int phase3 = MIN_PHASE_TIME; phase3 <= MAX_PHASE_TIME; ++phase3) {
                double[][] stats = goStats(new int[]{phase1, 10, phase3, 10}, arrivalTimesInit, SIMULATION_TIME, ITERATIONS);
                double individualMetric = getIndividualMetric(stats);
                if (!isSearching) {
                    System.out.printf("%.4f ", individualMetric);
                }
                if (individualMetric < minIndividualMetric) {
                    minIndividualMetric = individualMetric;
                    if (isSearching) {
                        System.out.printf("%.4f: [%d, %d, %d, %d]%n", minIndividualMetric, phase1, 10, phase3, 10);
                    }
                }
            }
            if (!isSearching) {
                System.out.println();
            }
        }
        return minIndividualMetric;
    }

    /**
     * Запуск симуляції руху на перехресті з використанням мереж Петрі.
     */
    public static double[][] goStats(int[] phaseTimes, double[] arrivalTimes, double simulationTime, int iterations) throws ExceptionInvalidTimeDelay {
        return IntStream.range(0, iterations)
                .mapToObj(_ -> {
                    try {
                        ArrayList<PetriSim> connectedSimulationModels = createSimulationModels(phaseTimes, arrivalTimes);
                        connectTrafficSubsystems(connectedSimulationModels);
                        PetriObjModel model = new PetriObjModel(connectedSimulationModels);
                        model.setIsProtokol(false);
                        model.go(simulationTime);
                        return getStatistics(model);
                    } catch (ExceptionInvalidTimeDelay e) {
                        System.err.printf("[ERROR] Invalid time delay: %s%n", e.getMessage());
                        return new double[8];
                    }
                })
                .toArray(double[][]::new);
    }

    /**
     * Отримання максимальної середньої кількості автомобілів, що очікують переїзду перехрестя.
     * Ця метрика використовується для оцінки ефективності роботи перехрестя (метрика індивіда популяції).
     */
    public static double getIndividualMetric(double[][] stats) {
        return Arrays.stream(IntStream.range(0, 4)
                        .mapToDouble(i -> Arrays.stream(stats)
                                .mapToDouble(stat -> stat[i])
                                .average()
                                .orElse(0))
                        .toArray())
                .max()
                .orElse(0);
    }

    /**
     * Виведення статистичних даних симуляції.
     */
    private static void printStatistics(double[][] stats) {
        double[] averages = new double[stats[0].length];
        for (int i = 0; i < stats[0].length; ++i) {
            int finalI = i;
            averages[i] = Arrays.stream(stats)
                    .mapToDouble(stat -> stat[finalI])
                    .average()
                    .orElse(0);
        }

        System.out.printf("%nСередня кількість автомобілів, що очікують переїзду перехрестя в різних напрямках в середньому за %d ітерацій:%n", ITERATIONS);
        for (int i = 0; i < 4; ++i) {
            System.out.printf(String.format("Напрямок %d: %.4f%n", i + 1, averages[i]));
        }

        System.out.printf("%nКількість автомобілів, що проїхало перехрестя в різних напрямках в середньому за %d ітерацій:%n", ITERATIONS);
        for (int i = 4; i < stats[0].length; ++i) {
            System.out.printf(String.format("Напрямок %d: %.4f%n", i - 3, averages[i]));
        }
    }

    /**
     * Отримання статистичних даних з моделі.
     */
    private static double[] getStatistics(PetriObjModel model) {
        return new double[]{
                model.getListObj().get(1).getNet().getListP()[1].getMean(),
                model.getListObj().get(2).getNet().getListP()[1].getMean(),
                model.getListObj().get(3).getNet().getListP()[1].getMean(),
                model.getListObj().get(4).getNet().getListP()[1].getMean(),
                model.getListObj().get(1).getNet().getListP()[2].getMark(),
                model.getListObj().get(2).getNet().getListP()[2].getMark(),
                model.getListObj().get(3).getNet().getListP()[2].getMark(),
                model.getListObj().get(4).getNet().getListP()[2].getMark(),
        };
    }

    /**
     * Створення моделей для симуляції: генератор, роботи та верстати.
     */
    public static @NotNull ArrayList<PetriSim> createSimulationModels(int[] phaseTimes, double[] arrivalTimes) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriSim> simulationModels = new ArrayList<>();

        // Додавання підсистеми управління
        simulationModels.add(new PetriSim(createManagementSubsystem(phaseTimes)));

        // Додавання підсистем руху транспорту для чотирьох напрямків
        for (int i = 0; i < 4; ++i) {
            simulationModels.add(new PetriSim(createDirectionalTrafficSubsystem(i, arrivalTimes[i])));
        }

        return simulationModels;
    }

    /**
     * З'єднання підсистем руху транспорту з підсистемою управління.
     */
    public static void connectTrafficSubsystems(@NotNull List<PetriSim> models) {
        // Перша та друга фази (зелене світло в 1 та 2 напрямках)
        models.get(1).getNet().getListP()[3] = models.get(0).getNet().getListP()[0];
        models.get(2).getNet().getListP()[3] = models.get(0).getNet().getListP()[0];

        // Третя та четверта фази (зелене світло в 3 та 4 напрямках)
        models.get(3).getNet().getListP()[3] = models.get(0).getNet().getListP()[5];
        models.get(4).getNet().getListP()[3] = models.get(0).getNet().getListP()[5];
    }

    /**
     * Створення мережі Петрі для підсистеми руху транспорту в одному напрямку.
     */
    private static @NotNull PetriNet createDirectionalTrafficSubsystem(int num, double arrivalTime) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriP> places = new ArrayList<>(List.of(
                new PetriP(String.format("Надходження №%d", num + 1), 1),
                new PetriP(String.format("Кількість автомобілів, що очікують переїзду перехрестя №%d", num + 1), 0),
                new PetriP(String.format("Всього автомобілів проїхало у напрямку №%d", num + 1), 0),
                new PetriP(String.format("Є зелене світло у напрямку №%d", num + 1), 0)
        ));

        ArrayList<PetriT> transitions = new ArrayList<>(List.of(
                new PetriT(String.format("Надходження автомобілів №%d", num + 1), arrivalTime),
                new PetriT(String.format("Переїзд перехрестя №%d", num + 1), 2.0)
        ));

        transitions.getFirst().setDistribution("exp", transitions.getFirst().getTimeServ());
        transitions.getFirst().setParamDeviation(0.0);

        PetriNet petriNet = getPetriNetDirectionalTrafficSubsystem(num, places, transitions);

        resetNextCounters();
        return petriNet;
    }

    /**
     * Створення мережі Петрі для підсистеми руху транспорту в одному напрямку.
     */
    @Contract("_, _, _ -> new")
    private static @NotNull PetriNet getPetriNetDirectionalTrafficSubsystem(int num, @NotNull ArrayList<PetriP> places, @NotNull ArrayList<PetriT> transitions) throws ExceptionInvalidTimeDelay {
        ArrayList<ArcIn> arcIns = new ArrayList<>(List.of(
                new ArcIn(places.get(0), transitions.get(0), 1),
                new ArcIn(places.get(1), transitions.get(1), 1),
                new ArcIn(places.get(3), transitions.get(1), 1)
        ));
        arcIns.get(2).setInf(true);

        ArrayList<ArcOut> arcOuts = new ArrayList<>(List.of(
                new ArcOut(transitions.get(0), places.get(0), 1),
                new ArcOut(transitions.get(0), places.get(1), 1),
                new ArcOut(transitions.get(1), places.get(2), 1)
        ));

        return new PetriNet(String.format("Підсистема руху автомобілів у напрямку №%d", num + 1), places, transitions, arcIns, arcOuts);
    }

    /**
     * Створення мережі Петрі для підсистеми управління світлофорами.
     */
    private static @NotNull PetriNet createManagementSubsystem(int @NotNull [] phaseTimes) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriP> places = new ArrayList<>(List.of(
                new PetriP("Є зелене світло в 1 та 2 напрямках", 1),
                new PetriP("До 2 фази", 1),
                new PetriP("До 3 фази", 0),
                new PetriP("До 4 фази", 0),
                new PetriP("До 1 фази", 0),
                new PetriP("Є зелене світло в 3 та 4 напрямках", 0)
        ));

        ArrayList<PetriT> transitions = new ArrayList<>(List.of(
                new PetriT("Зелене світло в 1 та 2 напрямках", phaseTimes[0]),
                new PetriT("Жовте світло в усіх напрямках №1", phaseTimes[1]),
                new PetriT("Зелене світло в 3 та 4 напрямках", phaseTimes[2]),
                new PetriT("Жовте світло в усіх напрямках №2", phaseTimes[3])
        ));

        PetriNet petriNet = getPetriNetManagementSubsystem(places, transitions);

        resetNextCounters();
        return petriNet;
    }

    /**
     * Створення мережі Петрі для підсистеми управління світлофорами.
     */
    private static @NotNull PetriNet getPetriNetManagementSubsystem(@NotNull ArrayList<PetriP> places, @NotNull ArrayList<PetriT> transitions) throws ExceptionInvalidTimeDelay {
        ArrayList<ArcIn> arcIns = new ArrayList<>(List.of(
                new ArcIn(places.get(1), transitions.get(1), 1),
                new ArcIn(places.get(2), transitions.get(2), 1),
                new ArcIn(places.get(3), transitions.get(3), 1),
                new ArcIn(places.get(4), transitions.get(0), 1),
                new ArcIn(places.get(0), transitions.get(1), 1),
                new ArcIn(places.get(5), transitions.get(3), 1)
        ));

        ArrayList<ArcOut> arcOuts = new ArrayList<>(List.of(
                new ArcOut(transitions.get(0), places.get(1), 1),
                new ArcOut(transitions.get(1), places.get(2), 1),
                new ArcOut(transitions.get(2), places.get(3), 1),
                new ArcOut(transitions.get(3), places.get(4), 1),
                new ArcOut(transitions.get(1), places.get(5), 1),
                new ArcOut(transitions.get(3), places.get(0), 1)
        ));

        return new PetriNet("Підсистема управління", places, transitions, arcIns, arcOuts);
    }

    /**
     * Скидання лічильників для об'єктів мережі Петрі.
     */
    private static void resetNextCounters() {
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
    }
}
