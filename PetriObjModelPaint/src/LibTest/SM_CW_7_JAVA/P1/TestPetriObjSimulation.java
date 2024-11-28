package LibTest.SM_CW_7_JAVA.P1;

import LibNet.NetLibrary;
import PetriObj.ExceptionInvalidNetStructure;
import PetriObj.ExceptionInvalidTimeDelay;
import PetriObj.PetriObjModel;
import PetriObj.PetriSim;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


/**
 * @author Inna V. Stetsenko
 */
public class TestPetriObjSimulation {
    // Результати збігаються з аналітичними обрахунками
    public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        // Цей фрагмент для запуску імітації моделі з заданою мережею Петрі на інтервалі часу timeModeling
        PetriObjModel model = getModel();
        model.setIsProtokol(false);
        double timeModeling = 1_000_000;
        model.go(timeModeling);

        // Цей фрагмент для виведення результатів моделювання на консоль
        System.out.println("Mean value of queue");
        for (int j = 1; j < 5; j++) {
            System.out.println(model.getListObj().get(j).getNet().getListP()[0].getMean());
        }
        System.out.println("Mean value of channel worked");
        for (int j = 1; j < 4; j++) {
            System.out.println(1.0 - model.getListObj().get(j).getNet().getListP()[1].getMean());
        }
        System.out.println(2.0 - model.getListObj().get(4).getNet().getListP()[1].getMean());

        System.out.println("Estimation precision");
        double[] valuesQueue = {1.786, 0.003, 0.004, 0.00001};

        System.out.println(" Mean value of queue  precision: ");
        for (int j = 1; j < 5; j++) {
            double inaccuracy = (model.getListObj().get(j).getNet().getListP()[0].getMean() - valuesQueue[j - 1]) / valuesQueue[j - 1] * 100;
            inaccuracy = Math.abs(inaccuracy);
            System.out.println(inaccuracy + " %");
        }

        double[] valuesChannel = {0.714, 0.054, 0.062, 0.036};

        System.out.println(" Mean value of channel worked  precision: ");

        for (int j = 1; j < 4; j++) {
            double inaccuracy = (1.0 - model.getListObj().get(j).getNet().getListP()[1].getMean() - valuesChannel[j - 1]) / valuesChannel[j - 1] * 100;
            inaccuracy = Math.abs(inaccuracy);

            System.out.println(inaccuracy + " %");
        }
        double inaccuracy = (2.0 - model.getListObj().get(4).getNet().getListP()[1].getMean() - valuesChannel[3]) / valuesChannel[3] * 100;
        inaccuracy = Math.abs(inaccuracy);

        System.out.println(inaccuracy + " %");
    }

    // Метод для конструювання моделі масового обслуговування з 4 СМО
    @Contract(" -> new")
    public static @NotNull PetriObjModel getModel() throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        ArrayList<PetriSim> list = new ArrayList<>();
        list.add(new PetriSim(NetLibrary.CreateNetGenerator(2.0)));
        list.add(new PetriSim(NetLibrary.CreateNetSMOwithoutQueue(1, 0.6, "First")));
        list.add(new PetriSim(NetLibrary.CreateNetSMOwithoutQueue(1, 0.3, "Second")));
        list.add(new PetriSim(NetLibrary.CreateNetSMOwithoutQueue(1, 0.4, "Third")));
        list.add(new PetriSim(NetLibrary.CreateNetSMOwithoutQueue(2, 0.1, "Forth")));
        list.add(new PetriSim(NetLibrary.CreateNetFork(0.15, 0.13, 0.3)));

        list.get(0).getNet().getListP()[1] = list.get(1).getNet().getListP()[0]; // gen = > SMO1
        list.get(1).getNet().getListP()[2] = list.get(5).getNet().getListP()[0]; // SMO1 = > fork

        list.get(5).getNet().getListP()[1] = list.get(2).getNet().getListP()[0]; // fork => SMO2
        list.get(5).getNet().getListP()[2] = list.get(3).getNet().getListP()[0]; // fork => SMO3
        list.get(5).getNet().getListP()[3] = list.get(4).getNet().getListP()[0]; // fork => SMO4

        list.get(2).getNet().getListP()[2] = list.get(1).getNet().getListP()[0]; // SMO2 => SMO1
        list.get(3).getNet().getListP()[2] = list.get(1).getNet().getListP()[0]; // SMO3 => SMO1
        list.get(4).getNet().getListP()[2] = list.get(1).getNet().getListP()[0]; // SMO4 => SMO1

        return new PetriObjModel(list);
    }
}
