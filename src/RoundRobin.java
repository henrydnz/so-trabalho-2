import java.util.ArrayList;
import java.util.List;

public class RoundRobin {
    private List<Process> ready;
    private List<Process> finalized;
    private List<Process> blocked;

    private int totalCPUTime;

    // espaco de tempo por processo
    private int quantum;
    private int currentQuantum;

    public RoundRobin(List<Process> processes, int quantum) {
        this.ready = processes;

        this.finalized = new ArrayList<>();
        this.blocked = new ArrayList<>();

        this.totalCPUTime = 0;

        this.quantum = quantum;
        this.currentQuantum = 0;
    }

    public void preempt(Process process){
        process.setProcessState(ProcessState.READY);

        this.ready.remove(process);
        this.ready.add(process);
    }

    public void markAsReady(Process process){
        process.setProcessState(ProcessState.READY);

        this.blocked.remove(process);
        this.ready.add(process);
    }

    public void blockProcess(Process process){
        process.setProcessState(ProcessState.BLOCKED);

        this.ready.remove(process);
        this.blocked.add(process);

        // quando um processo eh bloqueado a cpu eh liberada instantaneamente
        // o quantum vai resetar para 0 novamente e o proximo proecsso executa
        this.currentQuantum = 0;
    }

    public void finalizeProcess(Process process){
        process.setProcessState(ProcessState.FINALIZED);

        this.ready.remove(process);
        this.finalized.add(process);

        // quando um processo eh finalizado a cpu eh liberada instantaneamente
        // o quantum vai resetar para 0 novamente e o proximo proecsso executa
        this.currentQuantum = 0;
    }

    public void tick(){
        this.totalCPUTime ++;
        this.currentQuantum++;

        if(this.currentQuantum == this.quantum)
            this.currentQuantum = 0;
    }

    public void run(){
        while(!this.ready.isEmpty() &&  !this.blocked.isEmpty()){
            cpu();
            waitForIOEvent();
        }
    }

    public void cpu(){
        Process currentProcess = this.ready.getFirst();

        if(currentProcess.isReady())
            currentProcess.setProcessState(ProcessState.EXECUTING);

        tick();
        currentProcess.execute();

        if(currentProcess.isDone()) {
            finalizeProcess(currentProcess);
            return;
        }

        if(currentProcess.hasIOEvent() && currentProcess.requestIO()) {
            blockProcess(currentProcess);
            return;
        }

        if (currentQuantum == 0) preempt(currentProcess);
    }

    public void waitForIOEvent(){
        if (!this.blocked.isEmpty()) {
            Process currentProcess = this.blocked.getFirst();

            currentProcess.waitForIO();

            if(currentProcess.IOHasArrived()){
                currentProcess.resetIO();
                markAsReady(currentProcess);
            }
        }
    }

    public int getTotalCPUTime(){
        return totalCPUTime;
    }
}
