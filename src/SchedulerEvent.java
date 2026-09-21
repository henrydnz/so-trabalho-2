/**
 * @brief Registro de um evento ocorrido durante o escalonamento.
 * @param processId ID do processo associado ao evento.
 * @param type Tipo de evento ocorrido.
 * @param time Instante (tempo) do evento.
 * @param queueId ID da fila (opcional).
 */
public record SchedulerEvent(int processId, EventType type, int time, Integer queueId) {
    @Override
    public String toString() {
        return "[t=" + time + "] pid=" + processId + " " + type +
                (queueId != null ? " (queue ID " + queueId + ")" : "");
    }
}
