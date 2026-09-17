import java.util.ArrayList;
import java.util.List;

//  BUGS:
//  finished acaba com processos repetidos
//  processos marcados como waiting não mudam estado pra waiting
//  precisa saber se o pipeline ta certo

public class RoundRobin {
    private final List<Process> ready;
    private final List<Process> finished;
    private final List<Process> blocked;

    private Process executing;
    private Process waiting;

    private final int quantum;
    private int currentQuantum;

    public RoundRobin(int quantum) {
        this.executing = null;
        this.waiting = null;

        this.ready = new ArrayList<>();
        this.finished = new ArrayList<>();
        this.blocked = new ArrayList<>();

        this.quantum = quantum;
        this.currentQuantum = 0;
    }

    public void addReadyProcess(Process process) {
        if(this.ready.contains(process) ||
                process.getProcessState() == ProcessState.EXECUTING ||
                process.getProcessState() == ProcessState.FINALIZED ||
                process.getProcessState() == ProcessState.BLOCKED) return;

        process.setProcessState(ProcessState.READY);
        ready.add(process);
    }

    public void blockProcess(Process process){
        process.setProcessState(ProcessState.BLOCKED);
        this.blocked.add(process);
    }

    public void finalizeProcess(Process process){
        process.setProcessState(ProcessState.FINALIZED);
        this.finished.add(process);
    }

    private void getNextProcess(){
        this.executing = this.ready.removeFirst();
        this.executing.setProcessState(ProcessState.EXECUTING);
    }

    public void updateExecutingProcess(int CPUTime){
        if(this.executing == null) {
            if(this.ready.isEmpty()) return; // sem processos pra executar...
            getNextProcess();
        }

        this.executing.execute();

        System.out.println("executed p"+this.executing.getProcessID()+" - time: "+CPUTime);

        this.currentQuantum++;

        if(this.executing.isDone()) {
            System.out.println("finalized p"+this.executing.getProcessID()+" - time: "+CPUTime);
            finalizeProcess(this.executing);
            this.executing = null;
            this.currentQuantum = 0;
        } else if(this.executing.hasIOEvent() && this.executing.requestIO()) {
            System.out.println("blocked p"+this.executing.getProcessID()+" - time: "+CPUTime);
            blockProcess(this.executing);
            this.executing = null;
            this.currentQuantum = 0;
        } else if(this.currentQuantum == this.quantum) {
            System.out.println("preempted p"+this.executing.getProcessID()+" - time: "+CPUTime);
            ready.add(this.executing);
            this.executing = null;
            this.currentQuantum = 0;
        }
    }

    private void getNextWaitingProcess(){
        this.waiting = this.blocked.removeFirst();
        this.waiting.setProcessState(ProcessState.WAITING);
    }

    public void waitForIOEvent(int CPUTime){
        if(this.waiting == null){
            if(this.blocked.isEmpty()) return;  // nenhum processo espera I/O
            getNextWaitingProcess();
        }

        if(!this.waiting.isWaiting()){
            this.waiting.setProcessState(ProcessState.WAITING);
        }

        this.waiting.waitForIO();

//        System.out.println("waiting p"+this.waiting.getProcessID()+" - time: "+CPUTime);
//        System.out.println("waiting remaining time: " + this.waiting.getIORemainingTime());
//        System.out.println("state: " + this.waiting.getProcessState());

        if(this.waiting.IOHasArrived()) {
            System.out.println("unblocked p"+this.waiting.getProcessID()+" - time: "+CPUTime);
            this.waiting.resetIO();
            addReadyProcess(this.waiting);
            this.waiting = null;
        }
    }

    public boolean hasFinished(int processCount){ return finished.size() == processCount; }

    public List<Process> getReadyProcesses() { return ready; }
    public List<Process> getFinishedProcesses() { return finished; }
    public List<Process> getBlockedProcesses() { return blocked; }
}
