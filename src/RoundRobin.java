import java.util.ArrayList;
import java.util.List;

public class RoundRobin {
    private List<Process> ready;
    private List<Process> finalized;
    private List<Process> blocked;

    private Process executing;
    private Process waiting;

    private int quantum;
    private int currentQuantum;

    public RoundRobin(int quantum) {
        this.executing = null;
        this.waiting = null;

        this.ready = new ArrayList<>();
        this.finalized = new ArrayList<>();
        this.blocked = new ArrayList<>();

        this.quantum = quantum;
        this.currentQuantum = 0;
    }

    public void addReadyProcess(Process process) {
        process.setProcessState(ProcessState.READY);
        ready.add(process);
    }

    public void unblockProcess(Process process){
        process.resetIO();
        this.blocked.remove(process);
        addReadyProcess(process);
    }

    public void blockProcess(Process process){
        process.setProcessState(ProcessState.BLOCKED);
        this.blocked.add(process);
    }

    public void finalizeProcess(Process process){
        process.setProcessState(ProcessState.FINALIZED);
        this.finalized.add(process);
    }

    public void getNextProcess(){
        this.executing = this.ready.removeFirst();
        this.executing.setProcessState(ProcessState.EXECUTING);
    }

    public void updateExecutingProcess(){
        if(this.executing == null) {
            if(this.ready.isEmpty()) return; // sem processos pra executar...
            getNextProcess();
        }

        this.executing.execute();
        this.currentQuantum++;

        if(this.executing.isDone()) {
            finalizeProcess(this.executing);
            this.executing = null;
            this.currentQuantum = 0;
        } else if(this.executing.hasIOEvent() && this.executing.requestIO()) {
            blockProcess(this.executing);
            this.executing = null;
            this.currentQuantum = 0;
        } else if(this.currentQuantum == this.quantum) {
            addReadyProcess(this.executing);
            this.executing = null;
            this.currentQuantum = 0;
        }
    }

    public void getNextWaitingProcess(){
        this.waiting = this.blocked.removeFirst();
        this.waiting.setProcessState(ProcessState.WAITING);
    }

    public void waitForIOEvent(){
        if(this.waiting == null){
            if(this.blocked.isEmpty()) return;  // nenhum processo espera I/O
            getNextWaitingProcess();
        }

        this.waiting.waitForIO();

        if(this.waiting.IOHasArrived()) {
            addReadyProcess(this.waiting);
            this.waiting = null;
        }
    }

    public boolean hasFinished(int processCount){ return finalized.size() == processCount; }

    public List<Process> getReady() { return ready; }

    public List<Process> getFinalized() { return finalized; }

    public List<Process> getBlocked() { return blocked; }
}
