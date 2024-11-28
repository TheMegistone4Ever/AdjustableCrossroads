package LibTest.SM_CW_7_JAVA.P3;

import PetriObj.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static LibTest.SM_CW_7_JAVA.P2.T2.resetNextCounters;
import static LibTest.SM_CW_7_JAVA.Utils.Constants.*;

/**
 * Клас для моделювання роботи автобусного підприємства з використанням мереж Петрі.
 * Симулює рух автобусів на маршрутах, посадку пасажирів та облік доходів.
 */
public class T3 {
    /**
     * Головний метод для запуску симуляції роботи автобусного підприємства.
     */
    public static void main(String[] args) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriSim> simulationModels = createSimulationModels();
        connectPetriNets(simulationModels);

        PetriObjModel model = new PetriObjModel(simulationModels);
        model.setIsProtokol(false);
        model.go(SIMULATION_TIME[1]);

        printStatistics(model);
    }

    /**
     * Друк статистичних даних симуляції.
     * Виводить загальний виторг, втрату прибутку, середні значення черг та завантаженості каналів.
     */
    private static void printStatistics(PetriObjModel model) {
        System.out.printf("Загальна виручка автопідприємства: %d%n", countTotalRevenue(model));

        System.out.printf("Загальна втрата прибутку: %d%n", countTotalLostRevenue(model));

        System.out.println("\nСереднє значення черги:");
        for (int j = 0; j < 6; j++) {
            PetriP queue = model.getListObj().get(j).getNet().getListP()[2];
            System.out.printf("Об'єкт \"%s\": %.4f%n", queue.getName(), queue.getMean());
        }

        System.out.println("\nСереднє значення завантаженості каналів:");
        for (int j = 0; j < 6; j++) {
            PetriP channel = model.getListObj().get(j).getNet().getListP()[1];
            System.out.printf("Об'єкт \"%s\": %.4f%n", channel.getName(), 1.0 - channel.getMean());
        }
    }

    /**
     * Підрахунок загального виторгу підприємства.
     */
    private static int countTotalRevenue(PetriObjModel model) {
        int totalRevenue = 0;
        for (int j = 0; j < 6; j++) {
            if (j % 3 != 0) {
                totalRevenue += model.getListObj().get(j).getNet().getListP()[4].getMark();
            }
        }
        return totalRevenue;
    }

    /**
     * Підрахунок загальної втрати прибутку.
     */
    private static int countTotalLostRevenue(PetriObjModel model) {
        int totalLostRevenue = 0;
        for (int j = 0; j < 6; j++) {
            if (j % 3 == 0) {
                totalLostRevenue += model.getListObj().get(j).getNet().getListP()[2].getMark();
            }
        }
        return totalLostRevenue;
    }

    /**
     * Створення моделей для симуляції: зупинки та автобусні маршрути.
     */
    private static @NotNull ArrayList<PetriSim> createSimulationModels() throws ExceptionInvalidTimeDelay {
        return new ArrayList<>(List.of(
                new PetriSim(CreateNetBusStop("№1")),

                new PetriSim(CreateNetBusRoute("A №1", 5, BUS_INITIAL_COUNTS[0], BUS_INTERVALS[0])),
                new PetriSim(CreateNetBusRoute("A №2", 5, BUS_INITIAL_COUNTS[0], BUS_INTERVALS[0])),

                new PetriSim(CreateNetBusStop("№2")),

                new PetriSim(CreateNetBusRoute("B №1", 0, BUS_INITIAL_COUNTS[1], BUS_INTERVALS[1])),
                new PetriSim(CreateNetBusRoute("B №2", 0, BUS_INITIAL_COUNTS[1], BUS_INTERVALS[1]))
        ));
    }

    /**
     * З'єднання мереж Петрі для взаємодії зупинок та маршрутів.
     */
    private static void connectPetriNets(@NotNull List<PetriSim> models) {
        models.get(0).getNet().getListP()[3] = models.get(1).getNet().getListP()[2]; // черга на зупинці 1 -> черга на маршруті A.1
        models.get(3).getNet().getListP()[3] = models.get(2).getNet().getListP()[2]; // черга на зупинці 2 -> черга на маршруті A.2
        models.get(1).getNet().getListP()[1] = models.get(2).getNet().getListP()[5]; // автобус "А" вільний 2 -> автобус "А" вільний 1
        models.get(2).getNet().getListP()[1] = models.get(1).getNet().getListP()[5]; // автобус "А" вільний 1 -> автобус "А" вільний 2

        models.get(0).getNet().getListP()[3] = models.get(4).getNet().getListP()[2]; // черга на зупинці 1 -> черга на маршруті B.1
        models.get(3).getNet().getListP()[3] = models.get(5).getNet().getListP()[2]; // черга на зупинці 2 -> черга на маршруті B.2
        models.get(4).getNet().getListP()[1] = models.get(5).getNet().getListP()[5]; // автобус "В" вільний 2 -> автобус "В" вільний 1
        models.get(5).getNet().getListP()[1] = models.get(4).getNet().getListP()[5]; // автобус "В" вільний 1 -> автобус "В" вільний 2
    }

    /**
     * Створення мережі Петрі для зупинки.
     *
     * @param name Назва зупинки
     * @return Мережа Петрі для зупинки
     */
    public static @NotNull PetriNet CreateNetBusStop(String name) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriP> places = new ArrayList<>(List.of(
                new PetriP(String.format("Зупинка: %s", name), 0),
                new PetriP(String.format("Надходження пасажирів: %s", name), 1),
                new PetriP(String.format("Втрачений прибуток: %s", name), 0),
                new PetriP(String.format("Черга: %s", name), 0)
        ));

        ArrayList<PetriT> transitions = new ArrayList<>(List.of(
                new PetriT(String.format("Обрати інший маршрут: %s", name), 0.0),
                new PetriT(String.format("Зайняти чергу: %s", name), 0.0),
                createBusPassengerArrivalTransition(name)
        ));
        transitions.get(0).setPriority(5);

        ArrayList<ArcIn> arcIns = new ArrayList<>(List.of(
                new ArcIn(places.get(1), transitions.get(2), 1),
                new ArcIn(places.get(0), transitions.get(1), 1),
                new ArcIn(places.get(0), transitions.get(0), 1),
                createInfiniteLostRevenueArcIn(places, transitions)
        ));

        ArrayList<ArcOut> arcOuts = new ArrayList<>(List.of(
                new ArcOut(transitions.get(2), places.get(1), 1),
                new ArcOut(transitions.get(2), places.get(0), 1),
                new ArcOut(transitions.get(1), places.get(3), 1),
                new ArcOut(transitions.get(0), places.get(2), 20)
        ));

        PetriNet petriNet = new PetriNet(String.format("Зупинка: %s", name), places, transitions, arcIns, arcOuts);

        resetNextCounters();
        return petriNet;
    }

    /**
     * Створення переходу для надходження пасажирів з уніформним розподілом.
     */
    private static @NotNull PetriT createBusPassengerArrivalTransition(String name) {
        PetriT transition = new PetriT(String.format("Надходження: %s", name), 0.5);
        transition.setDistribution("unif", transition.getTimeServ());
        transition.setParamDeviation(0.2);
        return transition;
    }

    /**
     * Створення нескінченного вхідної дуги для обліку втраченого прибутку.
     */
    private static @NotNull ArcIn createInfiniteLostRevenueArcIn(@NotNull ArrayList<PetriP> places, @NotNull ArrayList<PetriT> transitions) {
        ArcIn arcIn = new ArcIn(places.get(3), transitions.get(0), 30);
        arcIn.setInf(true);
        return arcIn;
    }

    /**
     * Створення мережі Петрі для автобусного маршруту.
     *
     * @param name            Назва маршруту
     * @param priority        Пріоритет маршруту
     * @param initialBusCount Початкова кількість автобусів
     * @param busInterval     Інтервал руху автобусів
     * @return Мережа Петрі для автобусного маршруту
     */
    public static @NotNull PetriNet CreateNetBusRoute(String name, int priority, int initialBusCount, double busInterval)
            throws ExceptionInvalidTimeDelay {
        ArrayList<PetriP> places = new ArrayList<>(List.of(
                new PetriP(String.format("Маршрут пройдено: %s", name), 0),
                new PetriP(String.format("Вільний у кінці: %s", name), 0),
                new PetriP(String.format("Черга: %s", name), 0),
                new PetriP(String.format("Посадка виконана: %s", name), 0),
                new PetriP(String.format("Виручка автопідприємства: %s", name), 0),
                new PetriP(String.format("Вільний на початку: %s", name), initialBusCount)
        ));

        ArrayList<PetriT> transitions = new ArrayList<>(List.of(
                createBusReleaseTransition(name),
                createBusLoadingTransition(name, priority),
                createBusRouteTransition(name, busInterval)
        ));

        PetriNet petriNet = getPetriBusRoute(name, places, transitions);

        resetNextCounters();
        return petriNet;
    }

    private static @NotNull PetriNet getPetriBusRoute(String name, @NotNull ArrayList<PetriP> places, @NotNull ArrayList<PetriT> transitions) throws ExceptionInvalidTimeDelay {
        ArrayList<ArcIn> arcIns = new ArrayList<>(List.of(
                new ArcIn(places.get(2), transitions.get(1), BUS_CAPACITY),
                new ArcIn(places.get(5), transitions.get(2), 1),
                new ArcIn(places.get(0), transitions.get(0), 1),
                new ArcIn(places.get(5), transitions.get(1), 1)
        ));

        ArrayList<ArcOut> arcOuts = new ArrayList<>(List.of(
                new ArcOut(transitions.get(2), places.get(4), BUS_CAPACITY * 20),
                new ArcOut(transitions.get(1), places.get(5), 1),
                new ArcOut(transitions.get(2), places.get(0), 1),
                new ArcOut(transitions.get(0), places.get(1), 1)
        ));

        return new PetriNet(String.format("Автобусний маршрут: %s", name), places, transitions, arcIns, arcOuts);
    }

    /**
     * Створення переходу для звільнення автобуса.
     */
    private static @NotNull PetriT createBusReleaseTransition(String name) {
        PetriT transition = new PetriT(String.format("Звільнення: %s", name), 5.0);
        transition.setDistribution("unif", transition.getTimeServ());
        transition.setParamDeviation(1.0);
        return transition;
    }

    /**
     * Створення переходу для посадки пасажирів з пріоритетом.
     */
    private static @NotNull PetriT createBusLoadingTransition(String name, int priority) {
        PetriT transition = new PetriT(String.format("Посадка на: %s", name), 0.0);
        transition.setPriority(priority);
        return transition;
    }

    /**
     * Створення переходу для проходження маршруту.
     */
    private static @NotNull PetriT createBusRouteTransition(String name, double busInterval) {
        PetriT transition = new PetriT(String.format("Проходження маршруту: %s", name), busInterval);
        transition.setDistribution("unif", transition.getTimeServ());
        transition.setParamDeviation(5.0);
        return transition;
    }
}
