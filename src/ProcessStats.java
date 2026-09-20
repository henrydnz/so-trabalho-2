// informações da simulação por processo.
//      arrival time: tempo em que o processo chega no sistema .
//      cpu total time: tempo total em que o processo fica na cpu.
//      first execution time: tempo em que o processo é executado pela 1a vez.
//      completion time: tempo em que o processo é finalizado
//      turnaround time: tempo total desde que o processo chegou no sistema até completar
//      waiting time: tempo total em que o processo ficou esperando na fila de processos prontos (ready).
//      response time: tempo desde que o processo chegou no sistema até a primeira execução.
//      context switches: quantidade de vezes em que o processo foi preemptado
//      total io time: tempo total em que o processo ficou bloqueado esperando E/S

public record ProcessStats(int pid, String processName, int arrivalTime, int cpuTotalTime, int firstExecutionTime,
                           int completionTime, int turnaroundTime, int waitingTime, int responseTime,
                           int contextSwitches, int totalIOTime) {

    @Override
    public String toString() {
        return String.format(
                "PID %-3d %-18s | arrivalTime=%-4d cpuTotalTime=%-4d firstExecutinTime=%-4d completionTime=%-4d | " +
                        "turnaroundTime=%-4d waitingTime=%-4d responseTime=%-4d | contextSwitches=%-3d totalIOTime=%-4d",
                pid, processName, arrivalTime, cpuTotalTime, firstExecutionTime, completionTime,
                turnaroundTime, waitingTime, responseTime, contextSwitches, totalIOTime
        );
    }
}