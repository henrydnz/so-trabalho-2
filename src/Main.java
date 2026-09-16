

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();

        String processListFilename = "";
        simulator.readProcessList(processListFilename);

        simulator.runSimulation(0);

    }
}