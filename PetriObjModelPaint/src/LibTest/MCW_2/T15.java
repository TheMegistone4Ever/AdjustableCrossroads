package LibTest.MCW_2;

import PetriObj.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class T15 {
    private static final double SIMULATION_TIME = 1000.;
    private static final int CAMERA_COUNT = 600;
    private static final int BUFFER_SIZE = 100;

    public static void main(String[] args) throws ExceptionInvalidTimeDelay, ExceptionInvalidNetStructure {
        ArrayList<PetriSim> simulationModels = createSimulationModels(BUFFER_SIZE);
        connectModels(simulationModels);

        PetriObjModel model = new PetriObjModel(simulationModels);
        model.setIsProtokol(false);
        model.go(SIMULATION_TIME);

        printStatistics(model);
    }

    private static void connectModels(@NotNull ArrayList<PetriSim> simulationModels) {
        for (int i = 0; i < simulationModels.size() - 2; i++) {
            simulationModels.get(i).getNet().getListP()[1] = simulationModels.get(simulationModels.size() - 2).getNet().getListP()[0];
        }

        simulationModels.get(simulationModels.size() - 2).getNet().getListP()[2] = simulationModels.getLast().getNet().getListP()[0];
    }

    private static void printStatistics(@NotNull PetriObjModel model) {
        System.out.println("Статистика симуляції:");
        System.out.printf("Всього оброблено зображень: %d\n", model.getListObj().getLast().getNet().getListP()[2].getMark());
    }

    private static @NotNull ArrayList<PetriSim> createSimulationModels(int buffer) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriSim> models = new ArrayList<>();

        for (int i = 0; i < CAMERA_COUNT; i++) {
            PetriNet cameraNet = createCameraNet(i + 1);
            models.add(new PetriSim(cameraNet));
        }

        models.add(new PetriSim(CreateNetServer(buffer)));

        models.add(new PetriSim(CreateNetDB()));

        return models;
    }

    private static @NotNull PetriNet createCameraNet(int cameraId) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriP> d_P = new ArrayList<>();
        ArrayList<PetriT> d_T = new ArrayList<>();
        ArrayList<ArcIn> d_In = new ArrayList<>();
        ArrayList<ArcOut> d_Out = new ArrayList<>();

        d_P.add(new PetriP("P1" + cameraId, 1));
        d_P.add(new PetriP("P2" + cameraId, 0));

        PetriT generatePacket = new PetriT("Надходження запитів з камери_" + cameraId, 60.0);
        generatePacket.setDistribution("exp", generatePacket.getTimeServ());
        generatePacket.setParamDeviation(0.0);
        d_T.add(generatePacket);

        d_In.add(new ArcIn(d_P.get(0), generatePacket, 1));
        d_Out.add(new ArcOut(generatePacket, d_P.get(0), 1));
        d_Out.add(new ArcOut(generatePacket, d_P.get(1), 10));

        PetriNet net = new PetriNet("Камера_" + cameraId, d_P, d_T, d_In, d_Out);

        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();

        return net;
    }

    public static @NotNull PetriNet CreateNetServer(int buffer) throws ExceptionInvalidTimeDelay {
        ArrayList<PetriP> d_P = new ArrayList<>();
        ArrayList<PetriT> d_T = new ArrayList<>();
        ArrayList<ArcIn> d_In = new ArrayList<>();
        ArrayList<ArcOut> d_Out = new ArrayList<>();

        // Черга для 600 камер
        d_P.add(new PetriP("Черга зображень", 0)); // Збільшена черга для 600 камер
        d_P.add(new PetriP("P2", 0));
        d_P.add(new PetriP("P3", 0));
        d_P.add(new PetriP("P4", 0));
        d_P.add(new PetriP("Зайнято ресурсів", 0));
        d_P.add(new PetriP("Відмов всього", 0));

        d_T.add(new PetriT("Початок обробки", 0.0));
        d_T.add(new PetriT("Обробка сервером", 2.0));
        d_T.get(1).setDistribution("exp", d_T.get(1).getTimeServ());
        d_T.get(1).setParamDeviation(0.0);

        d_T.add(new PetriT("До БД", 0.01));
        d_T.get(2).setDistribution("exp", d_T.get(2).getTimeServ());
        d_T.get(2).setParamDeviation(0.0);

        d_T.add(new PetriT("Відмова", 0.0));
        d_T.get(3).setPriority(5);

        d_In.add(new ArcIn(d_P.get(0), d_T.get(0), 1));
        d_In.add(new ArcIn(d_P.get(1), d_T.get(1), 1));
        d_In.add(new ArcIn(d_P.get(2), d_T.get(2), 1));
        d_In.add(new ArcIn(d_P.get(4), d_T.get(2), 1));
        d_In.add(new ArcIn(d_P.get(4), d_T.get(3), buffer));
        d_In.get(4).setInf(true);
        d_In.add(new ArcIn(d_P.get(0), d_T.get(3), 1));

        d_Out.add(new ArcOut(d_T.get(0), d_P.get(1), 1));
        d_Out.add(new ArcOut(d_T.get(1), d_P.get(2), 1));
        d_Out.add(new ArcOut(d_T.get(2), d_P.get(3), 1));
        d_Out.add(new ArcOut(d_T.get(0), d_P.get(4), 1));
        d_Out.add(new ArcOut(d_T.get(3), d_P.get(5), 1));

        PetriNet d_Net = new PetriNet("mcw_15_server", d_P, d_T, d_In, d_Out);

        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();

        return d_Net;
    }

    public static @NotNull PetriNet CreateNetDB() throws ExceptionInvalidTimeDelay {
        ArrayList<PetriP> d_P = new ArrayList<>();
        ArrayList<PetriT> d_T = new ArrayList<>();
        ArrayList<ArcIn> d_In = new ArrayList<>();
        ArrayList<ArcOut> d_Out = new ArrayList<>();

        d_P.add(new PetriP("Черга до бд", 0));
        d_P.add(new PetriP("Чи БД в дії", 1));
        d_P.add(new PetriP("Всього записано", 0));

        d_T.add(new PetriT("Запис", 0.0));

        d_In.add(new ArcIn(d_P.get(0), d_T.get(0), 1));
        d_In.add(new ArcIn(d_P.get(1), d_T.get(0), 1));

        d_Out.add(new ArcOut(d_T.get(0), d_P.get(1), 1));
        d_Out.add(new ArcOut(d_T.get(0), d_P.get(2), 1));

        PetriNet d_Net = new PetriNet("mcw_15_db", d_P, d_T, d_In, d_Out);

        PetriP.initNext();
        PetriT.initNext();
        ArcIn.initNext();
        ArcOut.initNext();

        return d_Net;
    }
}
