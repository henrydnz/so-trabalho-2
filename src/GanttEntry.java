/**
 * @brief Registro que representa uma entrada no gráfico de Gantt.
 * @param pid Identificador do processo.
 * @param processName Nome do processo.
 * @param startTime Tempo de início da execução na CPU.
 * @param endTime Tempo de fim da execução na CPU.
 */
public record GanttEntry(int pid, String processName, int startTime, int endTime) {
    /**
     * @brief Calcula a duração do processo na CPU.
     * @return Duração da execução (endTime - startTime).
     */
    public int getDuration() { return endTime - startTime; }
}