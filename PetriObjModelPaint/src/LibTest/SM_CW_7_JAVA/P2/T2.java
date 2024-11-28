package LibTest.SM_CW_7_JAVA.P2;

import LibNet.NetLibrary;
import PetriObj.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static LibTest.SM_CW_7_JAVA.Utils.Constants.*;


/**
 * Клас для моделювання виробничого процесу з використанням мереж Петрі.
 * Симулює послідовну обробку деталей з генерацією, переміщенням та обробкою.
 */
public class T2 {
    /**
     * Головний метод для запуску симуляції виробничого процесу.
     */
    public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        ArrayList<PetriSim> simulationModels = createSimulationModels();
        connectPetriNets(simulationModels);

        PetriObjModel model = new PetriObjModel(simulationModels);
        model.setIsProtokol(false);
        model.go(SIMULATION_TIME[0]);

        printStatistics(model);
    }

    /**
     * Друк статистичних даних симуляції.
     * Виводить загальну кількість деталей, середні значення черг та завантаженості каналів.
     */
    private static void printStatistics(PetriObjModel model) {
        System.out.println("Загальна кількість деталей: " + countTotalDetails(model));

        System.out.println("\nСереднє значення черги:");
        for (int j = 1; j < 5; j++) {
            PetriP queue = model.getListObj().get(j).getNet().getListP()[0];
            System.out.printf("Об'єкт \"%s\": %.4f%n", queue.getName(), queue.getMean());
        }

        System.out.println("\nСереднє значення завантаженості каналів:");
        for (int j = 1; j < 5; j++) {
            PetriP channel = model.getListObj().get(j).getNet().getListP()[1];
            System.out.printf("Об'єкт \"%s\": %.4f%n", channel.getName(), 1. - channel.getMean());
        }
    }

    /**
     * Створення моделей для симуляції: генератор, роботи та верстати.
     */
    private static @NotNull ArrayList<PetriSim> createSimulationModels() throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        return new ArrayList<>(List.of(
                new PetriSim(NetLibrary.CreateNetGenerator(GENERATOR_TIME_MEAN)),
                new PetriSim(createRobotMovement(MOVE_TIMES[0])),

                new PetriSim(createProcessingStation(PROCESS_TIMES[0], PROCESS_DISTRIBUTIONS[0])),
                new PetriSim(createRobotMovement(MOVE_TIMES[1])),

                new PetriSim(createProcessingStation(PROCESS_TIMES[1], PROCESS_DISTRIBUTIONS[1])),
                new PetriSim(createRobotMovement(MOVE_TIMES[2]))
        ));
    }

    /**
     * З'єднання мереж Петрі для послідовної обробки деталей.
     */
    private static void connectPetriNets(@NotNull List<PetriSim> models) {
        models.get(0).getNet().getListP()[1] = models.get(1).getNet().getListP()[4]; // генератор -> робот1
        models.get(1).getNet().getListP()[5] = models.get(2).getNet().getListP()[1]; // робот1 -> верстат1

        models.get(2).getNet().getListP()[2] = models.get(3).getNet().getListP()[4]; // верстат1 -> робот2
        models.get(3).getNet().getListP()[5] = models.get(4).getNet().getListP()[1]; // робот2 -> верстат2

        models.get(4).getNet().getListP()[2] = models.get(5).getNet().getListP()[4]; // верстат2 -> робот3
    }

    /**
     * Підрахунок загальної кількості оброблених деталей.
     */
    private static int countTotalDetails(@NotNull PetriObjModel model) {
        return model.getListObj().get(5).getNet().getListP()[5].getMark();
    }

    /**
     * Створення мережі Петрі для моделювання руху робота.
     */
    private static @NotNull PetriNet createRobotMovement(double moveTime) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriP> places = new ArrayList<>(List.of(
                new PetriP("Вільний робот", 1),
                new PetriP("Завершена доставка", 0),
                new PetriP("Підібрано", 0),
                new PetriP("Завершено переміщення", 0),
                new PetriP("Доступні деталі", 0),
                new PetriP("Доставлено", 0)
        ));

        ArrayList<PetriT> transitions = new ArrayList<>(List.of(
                createNormalDistributionTransition("Підібрати", 8.),
                new PetriT("Повернутися", moveTime),
                createNormalDistributionTransition("Звільнити деталь", 8.),
                new PetriT("Перемістити деталь", 6.)
        ));

        PetriNet petriNet = getPetriNetRobotMovement(places, transitions);

        resetNextCounters();
        return petriNet;
    }

    private static @NotNull PetriNet getPetriNetRobotMovement(@NotNull ArrayList<PetriP> places, @NotNull ArrayList<PetriT> transitions) throws ExceptionInvalidTimeDelay {
        ArrayList<ArcIn> arcIns = new ArrayList<>(List.of(
                new ArcIn(places.get(0), transitions.get(0), 1),
                new ArcIn(places.get(1), transitions.get(1), 1),
                new ArcIn(places.get(2), transitions.get(3), 1),
                new ArcIn(places.get(3), transitions.get(2), 1),
                new ArcIn(places.get(4), transitions.get(0), 1)
        ));

        ArrayList<ArcOut> arcOuts = new ArrayList<>(List.of(
                new ArcOut(transitions.get(1), places.get(0), 1),
                new ArcOut(transitions.get(0), places.get(2), 1),
                new ArcOut(transitions.get(2), places.get(1), 1),
                new ArcOut(transitions.get(3), places.get(3), 1),
                new ArcOut(transitions.get(2), places.get(5), 1)
        ));

        return new PetriNet("переміщення_робота", places, transitions, arcIns, arcOuts);
    }

    /**
     * Створення мережі Петрі для моделювання обробної станції.
     */
    private static @NotNull PetriNet createProcessingStation(double processTime, String distribution) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriP> places = new ArrayList<>(List.of(
                new PetriP("Вільність верстату", 3),
                new PetriP("Деталей на обробку", 0),
                new PetriP("Оброблено деталей", 0)
        ));

        ArrayList<PetriT> transitions = new ArrayList<>(List.of(
                createDistributedTransition("Обробка", processTime, distribution)
        ));

        PetriNet petriNet = getPetriNetProcessingStation(places, transitions);

        resetNextCounters();
        return petriNet;
    }

    private static @NotNull PetriNet getPetriNetProcessingStation(@NotNull ArrayList<PetriP> places, @NotNull ArrayList<PetriT> transitions) throws ExceptionInvalidTimeDelay {
        ArrayList<ArcIn> arcIns = new ArrayList<>(List.of(
                new ArcIn(places.get(0), transitions.get(0), 1),
                new ArcIn(places.get(1), transitions.get(0), 1)
        ));

        ArrayList<ArcOut> arcOuts = new ArrayList<>(List.of(
                new ArcOut(transitions.get(0), places.get(0), 1),
                new ArcOut(transitions.get(0), places.get(2), 1)
        ));

        return new PetriNet("Обробка на верстаті", places, transitions, arcIns, arcOuts);
    }

    /**
     * Скидання лічильників для об'єктів мережі Петрі.
     */
    public static void resetNextCounters() {
        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();
    }

    /**
     * Створення переходу з нормальним розподілом часу.
     */
    private static @NotNull PetriT createNormalDistributionTransition(String name, double time) {
        PetriT transition = new PetriT(name, time);
        transition.setDistribution("norm", transition.getTimeServ());
        transition.setParamDeviation(1.0);
        return transition;
    }

    /**
     * Створення переходу з розподіленим часом.
     */
    private static @NotNull PetriT createDistributedTransition(String name, double time, String distribution) {
        PetriT transition = new PetriT(name, time);
        transition.setDistribution(distribution, transition.getTimeServ());
        transition.setParamDeviation(10.0);
        return transition;
    }
}
