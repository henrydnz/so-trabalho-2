// classe pra guardar um evento

public record SchedulerEvent(int processId, EventType type, int time, Integer queueId) {
    @Override
    public String toString() {
        return "[t=" + time + "] pid=" + processId + " " + type +
                (queueId != null ? " (queue ID " + queueId + ")" : "");
    }
}
