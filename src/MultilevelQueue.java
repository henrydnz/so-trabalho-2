import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MultilevelQueue {
    private static final int STARVATION_THRESHOLD = 30;

    private final RoundRobin queue1;
    private final RoundRobin queue2;
    private final RoundRobin queue3;

    private final List<SchedulerEvent> log;

    private RoundRobin activeQueue;

    public MultilevelQueue(int quantum01, int quantum02, int quantum03) {
        this.queue1 = new RoundRobin(quantum01, 1);
        this.queue2 = new RoundRobin(quantum02, 2);
        this.queue3 = new RoundRobin(quantum03, 3);
        this.log = new ArrayList<>();
        this.activeQueue = null;
    }

    public void addProcess(Process process, int currentTime) {
        ProcessType pt = process.getType();

        if (pt == ProcessType.REAL_TIME || pt == ProcessType.INTERACTIVE) {
            queue1.addReadyProcess(process, currentTime);
        } else if (pt == ProcessType.IO_BOUND || pt == ProcessType.MIXED) {
            queue2.addReadyProcess(process, currentTime);
        } else { // CPU_BOUND, BATCH
            queue3.addReadyProcess(process, currentTime);
        }
    }

    public void execute(int currentTime) {
        handleStarvation(currentTime);

        if (activeQueue == null || !activeQueue.isExecuting()) {
            activeQueue = pickNextQueue();
        }

        if (activeQueue != null) {
            activeQueue.updateExecutingProcess(currentTime);
        }

        queue1.waitForIOEvent(currentTime);
        queue2.waitForIOEvent(currentTime);
        queue3.waitForIOEvent(currentTime);
    }

    private RoundRobin pickNextQueue() {
        if (!queue1.getReadyProcesses().isEmpty()) return queue1;
        if (!queue2.getReadyProcesses().isEmpty()) return queue2;
        if (!queue3.getReadyProcesses().isEmpty()) return queue3;

        return null;
    }

    private void handleStarvation(int currentTime) {
        promote(queue3, queue2, currentTime);
        promote(queue2, queue1, currentTime);
    }

    private void promote(RoundRobin from, RoundRobin to, int currentTime) {
        List<Process> readySnapshot = new ArrayList<>(from.getReadyProcesses());

        for (Process process : readySnapshot) {
            int waitingTime = from.getWaitingTime(process.getProcessID(), currentTime);

            if (waitingTime >= STARVATION_THRESHOLD) {
                from.removeFromReady(process);
                to.addReadyProcess(process, currentTime);

                Integer targetQueueId = (to == queue1) ? 1 : 2;
                log.add(new SchedulerEvent(process.getProcessID(), EventType.PROMOTED, currentTime, targetQueueId));
            }
        }
    }

    public int getFinishedCount() {
        return queue1.getFinishedProcesses().size() + queue2.getFinishedProcesses().size() + queue3.getFinishedProcesses().size();
    }

    public List<Process> getFinished(){
        List<Process> combined = new ArrayList<>();

        combined.addAll(queue1.getFinishedProcesses());
        combined.addAll(queue2.getFinishedProcesses());
        combined.addAll(queue3.getFinishedProcesses());

        combined.sort(Comparator.comparingInt(Process::getProcessID));

        return combined;
    }

    public List<SchedulerEvent> getCombinedLog() {
        List<SchedulerEvent> combined = new ArrayList<>();

        combined.addAll(queue1.getLog());
        combined.addAll(queue2.getLog());
        combined.addAll(queue3.getLog());
        combined.addAll(this.log);

        combined.sort(Comparator.comparingInt(SchedulerEvent::time));

        return combined;
    }
}