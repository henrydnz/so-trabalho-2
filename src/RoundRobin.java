import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundRobin {
    private final List<Process> ready;
    private final List<Process> finished;
    private final List<Process> blocked;

    private final List<SchedulerEvent> log;

    private Process executing;
    private Process waiting;

    private final int quantum;
    private int currentQuantum;

    private final Integer queueId;
    private final Map<Integer, Integer> readySince;


    public RoundRobin(int quantum) {
        this(quantum, null);
    }

    public RoundRobin(int quantum, Integer queueId) {
        this.executing = null;
        this.waiting = null;

        this.ready = new ArrayList<>();
        this.finished = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.log = new ArrayList<>();
        this.readySince = new HashMap<>();

        this.quantum = quantum;
        this.currentQuantum = 0;
        this.queueId = queueId;
    }

    public void addToReady(Process process, int currentTime){
        process.setProcessState(ProcessState.READY);
        ready.add(process);
        readySince.put(process.getProcessID(), currentTime);
    }

    public void addReadyProcess(Process process, int currentTime) {
        if(ready.contains(process)) return;
        addToReady(process, currentTime);
    }

    public void blockProcess(Process process){
        process.setProcessState(ProcessState.BLOCKED);
        blocked.add(process);
    }

    public void finalizeProcess(Process process){
        process.setProcessState(ProcessState.FINISHED);
        finished.add(process);
    }

    public void preemptProcess(Process process){
        process.setProcessState(ProcessState.READY);
        ready.add(process);
    }

    private void getNextProcess(){
        executing = ready.removeFirst();
        executing.setProcessState(ProcessState.EXECUTING);
    }

    private void getNextWaitingProcess(){
        waiting = blocked.removeFirst();
        waiting.setProcessState(ProcessState.WAITING);
    }

    private void unblockProcess(Process process, int currentTime){
        process.resetIO();
        addToReady(process, currentTime);
    }

    private void newLog(Process process, EventType eventType, int time){
        log.add(new SchedulerEvent(process.getProcessID(), eventType, time,  this.queueId));
    }

    public void updateExecutingProcess(int CPUTime){
        if(executing == null) {
            if(ready.isEmpty()) return; // sem processos pra executar...
            getNextProcess();
            newLog(executing, EventType.STARTED_EXECUTING, CPUTime);
        }

        executing.execute();
        currentQuantum++;

        if(executing.isDone()) {
            finalizeProcess(executing);
            newLog(executing, EventType.FINISHED, CPUTime);
            executing = null;
            currentQuantum = 0;
        } else if(executing.hasIOEvent() && executing.requestIO()) {
            blockProcess(executing);
            newLog(executing, EventType.BLOCKED, CPUTime);
            executing = null;
            currentQuantum = 0;
        } else if(currentQuantum == quantum) {
            addToReady(executing, CPUTime);
            newLog(executing, EventType.PREEMPTED, CPUTime);
            executing = null;
            currentQuantum = 0;
        }
    }

    // TRATAMENTO DE E/S:
    //  - Um processo espera E/S por vez.
    //  - Processos bloqueados continuam bloqueados até o processo atual receber sua E/S.
    public void waitForIOEvent(int CPUTime){
        if(waiting == null){
            if(blocked.isEmpty()) return;
            getNextWaitingProcess();
        }

        waiting.waitForIO();

        if(waiting.IOHasArrived()) {
            unblockProcess(waiting, CPUTime);
            newLog(waiting, EventType.UNBLOCKED, CPUTime);
            waiting = null;
        }
    }

    public List<Process> getReadyProcesses() { return ready; }
    public List<Process> getFinishedProcesses() { return finished; }
    public List<Process> getBlockedProcesses() { return blocked; }

    public Process getExecutingProcess() { return executing; }
    public Process getWaitingProcess() { return waiting; }

    public boolean isExecuting(){ return this.executing != null; }

    public int getWaitingTime(int pid, int currentTime){
        Integer since = readySince.get(pid);
        return (since==null)? -1 : currentTime - since;
    }

    public void removeFromReady(Process process){
        ready.remove(process);
        readySince.remove(process.getProcessID());
    }

    public List<SchedulerEvent> getLog() { return log; }
}
