import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @brief Implementa a lógica de escalonamento Round Robin (chaveamento circular).
 */
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

    /**
     * @brief Construtor padrão da fila Round Robin.
     * @param quantum Limite de tempo (quantum) atribuído a cada processo.
     */
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

    /**
     * @brief Adiciona o processo à lista de prontos.
     * @param process O processo a ser colocado como PRONTO.
     * @param currentTime O tempo atual.
     */
    public void addToReady(Process process, int currentTime){
        process.setProcessState(ProcessState.READY);
        ready.add(process);
        readySince.put(process.getProcessID(), currentTime);
    }

    /**
     * @brief Adiciona um processo à fila de processos prontos, verificando se ele já não está nela.
     * @param process O processo a ser adicionado.
     * @param currentTime O tempo atual da simulação na CPU.
     */
    public void addReadyProcess(Process process, int currentTime) {
        if(ready.contains(process)) return;
        addToReady(process, currentTime);
    }

    /**
     * @brief Bloqueia um processo (envia para E/S).
     * @param process O processo a ser bloqueado.
     */
    public void blockProcess(Process process){
        process.setProcessState(ProcessState.BLOCKED);
        blocked.add(process);
    }

    /**
     * @brief Finaliza um processo concluído.
     * @param process O processo a finalizar.
     */
    public void finalizeProcess(Process process){
        process.setProcessState(ProcessState.FINISHED);
        finished.add(process);
    }

    /**
     * @brief Preempta o processo atual de volta para a fila de prontos.
     * @param process O processo preemptado.
     */
    public void preemptProcess(Process process){
        process.setProcessState(ProcessState.READY);
        ready.add(process);
    }

    /**
     * @brief Remove o próximo processo da fila de prontos e o define como em execução.
     */
    private void getNextProcess(){
        executing = ready.removeFirst();
        executing.setProcessState(ProcessState.EXECUTING);
    }

    /**
     * @brief Remove o próximo processo da fila de bloqueados e o define como aguardando E/S.
     */
    private void getNextWaitingProcess(){
        waiting = blocked.removeFirst();
        waiting.setProcessState(ProcessState.WAITING);
    }

    /**
     * @brief Desbloqueia um processo após finalizar a E/S, retornando-o para a fila de prontos.
     * @param process O processo a ser desbloqueado.
     * @param currentTime O tempo atual da simulação na CPU.
     */
    private void unblockProcess(Process process, int currentTime){
        process.resetIO();
        addToReady(process, currentTime);
    }

    /**
     * @brief Cria e registra um novo evento no log de escalonamento.
     * @param process O processo associado ao evento.
     * @param eventType O tipo de evento que ocorreu (ex: executando, bloqueado).
     * @param time O tempo exato em que o evento aconteceu.
     */
    private void newLog(Process process, EventType eventType, int time){
        log.add(new SchedulerEvent(process.getProcessID(), eventType, time,  this.queueId));
    }

    /**
     * @brief Atualiza a execução do processo atual na CPU.
     * @param CPUTime O tempo atual da CPU.
     */
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

    /**
     * @brief Atualiza os processos que estão à espera de E/S.
     * @param CPUTime O tempo atual da CPU.
     */
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

    /**
     * @brief Remove manualmente um processo da fila de prontos e limpa seu registro de tempo.
     * @param process O processo a ser removido.
     */
    public void removeFromReady(Process process){
        ready.remove(process);
        readySince.remove(process.getProcessID());
    }

    public List<SchedulerEvent> getLog() { return log; }
}
