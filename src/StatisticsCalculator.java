import java.util.*;

/**
 * @brief Classe responsável por calcular e compilar os dados analíticos da simulação.
 */
public class StatisticsCalculator {

    /**
     * @brief Calcula as estatísticas gerais e individuais de todos os processos.
     * @param log Lista de eventos de escalonamento registados.
     * @param processes Lista de processos a serem avaliados.
     * @return Um resumo completo (SimulationSummary) com métricas calculadas.
     */
    public SimulationSummary calculate(List<SchedulerEvent> log, List<Process> processes) {
        List<ProcessStats> statsList = new ArrayList<>();

        for (Process process : processes) {
            ProcessStats stats = calculateForProcess(process, log);
            statsList.add(stats);
        }

        double avgWaiting = statsList.stream()
                .mapToInt(ProcessStats::waitingTime)
                .average().orElse(0);

        double avgTurnaround = statsList.stream()
                .mapToInt(ProcessStats::turnaroundTime)
                .average().orElse(0);

        double avgResponse = statsList.stream()
                .mapToInt(ProcessStats::responseTime)
                .average().orElse(0);

        int totalContextSwitches = statsList.stream()
                .mapToInt(ProcessStats::contextSwitches)
                .sum();

        return new SimulationSummary(statsList, avgWaiting, avgTurnaround, avgResponse, totalContextSwitches);
    }

    /**
     * @brief Calcula as estatísticas detalhadas (turnaround, espera, trocas de contexto) para um único processo.
     * @param process O processo a ser analisado.
     * @param log A lista completa de eventos do escalonador.
     * @return Um objeto ProcessStats contendo todas as métricas isoladas deste processo.
     */
    private ProcessStats calculateForProcess(Process process, List<SchedulerEvent> log) {
        int pid = process.getProcessID();

        List<SchedulerEvent> processEvents = log.stream()
                .filter(e -> e.processId() == pid)
                .sorted(Comparator.comparingInt(SchedulerEvent::time))
                .toList();

        int firstExecutionTime = -1;
        int completionTime = -1;
        int contextSwitches = 0;
        int totalIOTime = 0;

        int lastBlockedTime = -1;

        for (SchedulerEvent event : processEvents) {
            switch (event.type()) {
                case STARTED_EXECUTING -> {
                    contextSwitches++;
                    if (firstExecutionTime == -1) {
                        firstExecutionTime = event.time();
                    }
                }
                case BLOCKED -> lastBlockedTime = event.time();
                case UNBLOCKED -> {
                    if (lastBlockedTime != -1) {
                        totalIOTime += event.time() - lastBlockedTime;
                        lastBlockedTime = -1;
                    }
                }
                case FINISHED -> completionTime = event.time();
                case PREEMPTED -> {}
            }
        }

        int arrivalTime = process.getSystemArrivalTime();
        int cpuTotalTime = process.getCPUDuration();

        int turnaroundTime = completionTime - arrivalTime;
        int waitingTime = turnaroundTime - cpuTotalTime - totalIOTime;
        int responseTime = firstExecutionTime - arrivalTime;

        return new ProcessStats(
                pid, process.getProcessName(), arrivalTime, cpuTotalTime,
                firstExecutionTime, completionTime,
                turnaroundTime, waitingTime, responseTime,
                contextSwitches, totalIOTime
        );
    }

    /**
     * @brief Constrói os dados necessários para plotar o Gráfico de Gantt da simulação.
     * @param log Lista de eventos de escalonamento.
     * @param processes Lista de processos.
     * @return Uma lista de entradas contendo tempos de CPU contínuos.
     */
    public List<GanttEntry> buildGanttChart(List<SchedulerEvent> log, List<Process> processes) {
        List<GanttEntry> entries = new ArrayList<>();

        Map<Integer, String> nameByPid = new HashMap<>();
        for (Process p : processes) {
            nameByPid.put(p.getProcessID(), p.getProcessName());
        }

        List<SchedulerEvent> sortedLog = log.stream()
                .sorted(Comparator.comparingInt(SchedulerEvent::time))
                .toList();

        Map<Integer, Integer> openStart = new HashMap<>();

        for (SchedulerEvent event : sortedLog) {
            int pid = event.processId();

            if (event.type() == EventType.STARTED_EXECUTING) {
                openStart.put(pid, event.time());
            } else if (event.type() == EventType.PREEMPTED
                    || event.type() == EventType.BLOCKED
                    || event.type() == EventType.FINISHED) {

                Integer start = openStart.get(pid);
                if (start != null) {
                    entries.add(new GanttEntry(pid, nameByPid.get(pid), start, event.time()));
                    openStart.remove(pid);
                }
            }
        }

        return entries;
    }
}