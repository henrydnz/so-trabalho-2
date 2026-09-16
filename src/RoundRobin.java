import java.util.ArrayList;
import java.util.List;

// TODO:

// ========= entrar processo no sistema
//      criar uma lógica pra só deixar o processo entrar no ready quando o
// tempo estipulado no processo (systemArrivalTime) chegar. Pra isso essa
// classe precisa conseguir enxergar a variável totalCPUTime dentro da classe
// Simulator.
// Nesse caso, da pra fazer isso percorrendo o processes e adicionando no ready
// o process que o systemArrivalTime for menor ou igual o totalCPUTime.

// ========= I/O
//      Precisa criar uma lógica pra colocar o processo esperando I/O na var
// waiting e usar ela na hora de fazer o wait for I/O.
// Nesse caso vai ser igual o executing. Quando um processo bloqueado tiver esperando
// uma IO, ele é removido de blocked e fica na variavel waiting. A funcao de espera
// (ou pode ser a funcao de update também) vai pegar esse processo e dar o wait.
// Se a espera acabar, o processo volta pro ready no mesmo tick de tempo (e muda
// o ProcessState).

//      No caso da multilevel queue, isso pode ser diferente, porque vou ter 3 round robins
// e so um processo vai poder ficar esperando I/O. Precisa ter visão do
// processo bloqueado e talvez por quanto tempo.

// ========= finalizando round robin
//      Da pra fazer uma flag aqui dentro pra saber quando o round robin acabou.
// nao e responsabilidade do Simulator lidar com isso. Mas o simulator
// precisa ter o loop, porque tem que rodar um tick apenas.
// isso é só retornar uma funcao bool finalized.size() == processCount;

public class RoundRobin {
    
    private List<Process> processes;
    private int processCount;

    private List<Process> ready;
    private List<Process> finalized;
    private List<Process> blocked;

    private Process executing;
    private Process waiting;

    // espaco de tempo por processo
    private int quantum;
    // quantum atual
    private int currentQuantum;

    public RoundRobin(List<Process> processes, int quantum) {
        this.processes = processes;
        this.processCount = processes.size();

        this.executing = null;
        this.waiting = null;

        this.ready = new ArrayList<>();
        this.finalized = new ArrayList<>();
        this.blocked = new ArrayList<>();

        this.quantum = quantum;
        this.currentQuantum = 0;
    }

    public void addProcess(Process process) {
        ready.add(process);
    }

    public void preempt(Process process){
        process.setProcessState(ProcessState.READY);

        this.ready.add(process);

        // duplicata
        this.executing = this.ready.removeFirst();
        this.executing.setProcessState(ProcessState.EXECUTING);
    }

    public void unblockProcess(Process process){
        process.setProcessState(ProcessState.READY);
        process.resetIO();

        this.blocked.remove(process);
        this.ready.add(process);
    }

    public void blockProcess(Process process){
        process.setProcessState(ProcessState.BLOCKED);

        this.ready.remove(process);
        this.blocked.add(process);
    }

    public void finalizeProcess(Process process){
        process.setProcessState(ProcessState.FINALIZED);

        // duplicata
        this.executing = this.ready.removeFirst();
        this.executing.setProcessState(ProcessState.EXECUTING);

        this.finalized.add(process);
    }

    public void updateExecutingProcess(){
        if(this.executing == null){
            if (this.ready.isEmpty()) return;

            // jogar snippet em uma funcao e substituir usos duplicados
            // depois tira elas das funcoes e deixa so aqui no update nas if guards
            // porque a responsabilidade vai ficar correta.
            this.executing = this.ready.removeFirst();
            this.executing.setProcessState(ProcessState.EXECUTING);
        }

        this.executing.execute();
        this.currentQuantum++;

        if(this.executing.isDone()) {
            finalizeProcess(this.executing);
            this.currentQuantum = 0;
            return;
        }

        if(this.executing.hasIOEvent() && this.executing.requestIO()) {
            blockProcess(this.executing);
            this.currentQuantum = 0;
            return;
        }

        if((this.currentQuantum == this.quantum) && !this.ready.isEmpty()) {
            preempt(this.executing);
            this.currentQuantum = 0;
        }
    }

    public void waitForIOEvent(){
        if (!this.blocked.isEmpty()) {
            Process currentProcess = this.blocked.getFirst();

            currentProcess.waitForIO();

            if(currentProcess.IOHasArrived())
                unblockProcess(currentProcess);
        }
    }

    public int getFinalizedProcessCount(){
        return finalized.size();
    }
}
