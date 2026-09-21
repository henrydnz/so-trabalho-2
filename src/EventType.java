/**
 * @brief Enumeração que representa os tipos de eventos gerados pelo escalonador.
 */
public enum EventType {
    STARTED_EXECUTING,
    PREEMPTED,
    BLOCKED,
    UNBLOCKED,
    FINISHED,
    PROMOTED
}
