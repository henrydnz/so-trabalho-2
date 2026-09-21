import java.io.PrintStream;
import java.util.List;

/**
 * @brief Registro que consolida os resultados estatísticos da simulação.
 * @param processStats Lista contendo as estatísticas individuais de cada processo.
 * @param averageWaitingTime Tempo de espera médio global.
 * @param averageTurnaroundTime Tempo de turnaround médio global.
 * @param averageResponseTime Tempo de resposta médio global.
 * @param totalContextSwitches Somatório das trocas de contexto.
 */
public record SimulationSummary(List<ProcessStats> processStats, double averageWaitingTime,
                                double averageTurnaroundTime, double averageResponseTime, int totalContextSwitches) {

//    public void printReport() {
//        System.out.println("=== Stats per Process ===");
//        for (ProcessStats ps : processStats) { System.out.println(ps); }
//        System.out.println();
//        System.out.printf("Average Waiting Time:   %.2f%n", averageWaitingTime);
//        System.out.printf("Average Turnaround Time:  %.2f%n", averageTurnaroundTime);
//        System.out.printf("Average Response Time: %.2f%n", averageResponseTime);
//        System.out.println("Total Context Switches: " + totalContextSwitches);
//    }
//
//    public void printGanttChart(List<GanttEntry> entries) {
//        System.out.println("=== Gantt Chart ===");
//        for (GanttEntry e : entries) {
//            System.out.printf("[%3d -> %3d] PID %-3d %s%n",
//                    e.startTime(), e.endTime(), e.pid(), e.processName());
//        }
//    }

    /**
     * @brief Escreve o relatório da simulação num arquivo.
     * @param out A stream de saída do arquivo.
     */
    public void printReportFile(PrintStream out) {
        out.println("=== General ===\n");
        out.printf("Average Waiting Time:   %.2f%n", averageWaitingTime());
        out.printf("Average Turnaround Time:  %.2f%n", averageTurnaroundTime());
        out.printf("Average Response Time: %.2f%n", averageResponseTime());
        out.println("Total Context Switches: " + totalContextSwitches());
        out.println("\n=== Stats per Process ===\n");
        for (ProcessStats ps : processStats) { out.println(ps); }
        out.println();
    }

    /**
     * @brief Escreve o gráfico de Gantt da simulação num arquivo.
     * @param out A stream de saída do arquivo.
     */
    public void printGanttChartFile(List<GanttEntry> entries, PrintStream out) {
        out.println("=== Gantt Chart ===\n");
        for (GanttEntry e : entries) {
            out.printf("[%3d -> %3d] PID %-3d %s%n",
                    e.startTime(), e.endTime(), e.pid(), e.processName());
        }
    }
}