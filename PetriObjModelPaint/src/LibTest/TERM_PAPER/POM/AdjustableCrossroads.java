package LibTest.TERM_PAPER.POM;

import PetriObj.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Клас для моделювання адаптивного перехрестя з використанням мереж Петрі.
 * Симулює рух транспорту з урахуванням світлофорного регулювання.
 */
public class AdjustableCrossroads {

    /**
     * Час моделювання руху транспорту.
     */
    public static final double SIMULATION_TIME = 1_000;

    /**
     * Головний метод для запуску симуляції руху на перехресті.
     */
    public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        double[] phaseTimes = {20.0, 10.0, 30.0, 10.0};
        double[] arrivalTimes = {15.0, 9.0, 20.0, 35.0};

        ArrayList<PetriSim> simulationModels = createSimulationModels(phaseTimes, arrivalTimes);
        connectTrafficSubsystems(simulationModels);

        PetriObjModel model = new PetriObjModel(simulationModels);
        model.setIsProtokol(false);
        model.go(SIMULATION_TIME);

        printStatistics(model);

        // Виведення максимальної середньої кількості очікування (метрика індивіда популяції)
        System.out.printf(String.format("\nМаксимальна кількість автомобілів, що очікують переїзду перехрестя: %.4f%n",
                getIndividualMetric(model)));
    }

    /**
     * Отримання максимальної середньої кількості автомобілів, що очікують переїзду перехрестя.
     * Ця метрика використовується для оцінки ефективності роботи перехрестя (метрика індивіда популяції).
     */
    public static double getIndividualMetric(PetriObjModel model) {
        return IntStream.range(1, model.getListObj().size())
                .mapToDouble(i -> model.getListObj().get(i).getNet().getListP()[1].getMean())
                .max()
                .orElse(0);
    }

    /**
     * Створення моделей для симуляції: генератор, роботи та верстати.
     */
    public static @NotNull ArrayList<PetriSim> createSimulationModels(double[] phaseTimes, double[] arrivalTimes) throws ExceptionInvalidTimeDelay {
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
     * Виведення статистичних даних симуляції.
     */
    private static void printStatistics(@NotNull PetriObjModel model) {
        // Виведення середньої кількості автомобілів, що очікують переїзду
        System.out.println("\nСередня кількість автомобілів, що очікують переїзду перехрестя в різних напрямках:");
        double[] meanValues = new double[4];
        for (int i = 1; i < model.getListObj().size(); ++i) {
            meanValues[i - 1] = model.getListObj().get(i).getNet().getListP()[1].getMean();
            System.out.printf(String.format("Напрямок %s: %.4f%n", model.getListObj().get(i).getName(), meanValues[i - 1]));
        }

        // Виведення кількості автомобілів, що проїхали перехрестя
        System.out.println("\nКількість автомобілів, що проїхало перехрестя в різних напрямках:");
        for (PetriSim objModel : model.getListObj()) {
            if (objModel.getNet().getListP().length > 4) {
                continue;
            }
            System.out.printf(String.format("Напрямок %s: %d%n", objModel.getName(), objModel.getNet().getListP()[2].getMark()));
        }
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
    private static @NotNull PetriNet createManagementSubsystem(double @NotNull [] phaseTimes) throws ExceptionInvalidTimeDelay {
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
