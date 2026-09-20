public record GanttEntry(int pid, String processName, int startTime, int endTime) {
    public int getDuration() { return endTime - startTime; }
}