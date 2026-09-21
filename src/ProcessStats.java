/**
 * @brief Registro que armazena as métricas finais de simulação para um processo específico.
 * @param pid ID do processo.
 * @param processName Nome do processo.
 * @param arrivalTime Tempo em que o processo chegou ao sistema.
 * @param cpuTotalTime Tempo total de execução necessário na CPU.
 * @param firstExecutionTime Tempo em que o processo executou pela primeira vez.
 * @param completionTime Tempo em que o processo finalizou.
 * @param turnaroundTime Tempo total decorrido desde a chegada até à finalização.
 * @param waitingTime Tempo total de espera nas filas de processos prontos.
 * @param responseTime Tempo desde a chegada até à primeira execução.
 * @param contextSwitches Número de trocas de contexto sofridas pelo processo.
 * @param totalIOTime Tempo total bloqueado à espera de E/S.
 */
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