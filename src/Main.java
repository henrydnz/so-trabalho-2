public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();

        String processListFilename = "processos_entrada_correlacionados.csv";

        simulator.readProcessList(processListFilename);

        simulator.runRoundRobin();

        System.out.println("total cpu time: " + simulator.getCPUTime() + " TU");
    }
}