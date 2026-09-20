import java.util.List;

// guarda o resumo das informações da simulação (calculado no StatisticsCalculator)
public record SimulationSummary(List<ProcessStats> processStats, double averageWaitingTime,
                                double averageTurnaroundTime, double averageResponseTime, int totalContextSwitches) {

    public void printReport() {
        System.out.println("=== Stats ===");
        for (ProcessStats ps : processStats) { System.out.println(ps); }
        System.out.println();
        System.out.printf("Average Waiting Time:   %.2f%n", averageWaitingTime);
        System.out.printf("Average Turnaround Time:  %.2f%n", averageTurnaroundTime);
        System.out.printf("Average Response Time: %.2f%n", averageResponseTime);
        System.out.println("Total Context Switches: " + totalContextSwitches);
    }

    public void printGanttChart(List<GanttEntry> entries) {
        System.out.println("=== Diagrama de Gantt ===");
        for (GanttEntry e : entries) {
            System.out.printf("[%3d -> %3d] PID %-3d %s%n",
                    e.startTime(), e.endTime(), e.pid(), e.processName());
        }
    }
}