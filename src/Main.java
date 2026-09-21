public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();

        String processListFilename = "processos_entrada_correlacionados.csv";

        simulator.readProcessList(processListFilename);
        simulator.runSimulation(0);
        System.out.println(simulator.getCPUTime());
        simulator.resetProcesses();

        simulator.readProcessList(processListFilename);
        simulator.runSimulation(1);
        System.out.println(simulator.getCPUTime());
        simulator.resetProcesses();

        simulator.readProcessList(processListFilename);
        simulator.runSimulation(2);
        System.out.println(simulator.getCPUTime());
    }
}