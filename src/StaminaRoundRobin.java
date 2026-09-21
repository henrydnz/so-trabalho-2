import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @brief Implementa um algoritmo customizado Round Robin baseado em 'stamina' (descanso).
 */
public class StaminaRoundRobin {
    private final List<Process> ready;
    private final List<Process> finished;
    private final List<Process> blocked;
    private final List<Process> rest;

    private final List<SchedulerEvent> log;

    private Process executing;
    private Process waiting;

    private final int quantum;
    private int currentQuantum;


    private final Map<Integer, Integer> cpuPasses;
    private final Map<Integer, Integer> timesRested;
    private final Map<Integer, Integer> restTimers;

    private final int MAX_PASSES = 20;
    private final int MAX_RESTS = 3;
    private final int REST_DURATION = 10;

    /**
     * @brief Inicializa o escalonador Stamina Round Robin.
     * @param quantum Quantum de CPU para execução normal.
     */
    public StaminaRoundRobin(int quantum) {
        this.executing = null;
        this.waiting = null;

        this.ready = new ArrayList<>();
        this.finished = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.rest = new ArrayList<>();
        this.log = new ArrayList<>();

        this.cpuPasses = new HashMap<>();
        this.timesRested = new HashMap<>();
        this.restTimers = new HashMap<>();

        this.quantum = quantum;
        this.currentQuantum = 0;
    }

    public void addToReady(Process process, int currentTime){
        process.setProcessState(ProcessState.READY);
        ready.add(process);
    }

    public void blockProcess(Process process){
        process.setProcessState(ProcessState.BLOCKED);
        blocked.add(process);
    }

    /**
     * @brief Envia um processo para um estado de descanso obrigatório para recuperar prioridade/stamina.
     * @param process O processo a descansar.
     * @param currentTime O tempo atual.
     */
    public void sendToRest(Process process, int currentTime) {
        process.setProcessState(ProcessState.WAITING);
        rest.add(process);
        restTimers.put(process.getProcessID(), REST_DURATION);
    }

    public void finalizeProcess(Process process){
        process.setProcessState(ProcessState.FINISHED);
        finished.add(process);
    }

    private void getNextProcess(){
        executing = ready.removeFirst();
        executing.setProcessState(ProcessState.EXECUTING);
    }

    private void newLog(Process process, EventType eventType, int time){
        log.add(new SchedulerEvent(process.getProcessID(), eventType, time, null));
    }

    /**
     * @brief Atualiza o tempo de descanso dos processos em repouso e os devolve à fila de prontos se o tempo esgotar.
     * @param currentTime O tempo atual da simulação na CPU.
     */
    private void updateRestingProcesses(int currentTime) {
        List<Process> recovered = new ArrayList<>();
        for(Process p : rest) {
            int pid = p.getProcessID();
            int time = restTimers.getOrDefault(pid, 0) - 1;

            if(time <= 0) {
                recovered.add(p);
            } else {
                restTimers.put(pid, time);
            }
        }

        for(Process p : recovered) {
            rest.remove(p);
            addToReady(p, currentTime);
        }
    }

    /**
     * @brief Atualiza a execução do processo e gere as passagens de CPU limitadas pela stamina.
     * @param CPUTime O tempo atual da CPU.
     */
    public void updateExecutingProcess(int CPUTime){
        updateRestingProcesses(CPUTime);

        if(executing == null) {
            if(ready.isEmpty()) return;
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
            cpuPasses.put(executing.getProcessID(), 0);
            blockProcess(executing);
            newLog(executing, EventType.BLOCKED, CPUTime);
            executing = null;
            currentQuantum = 0;

        } else if(currentQuantum == quantum) {
            int pid = executing.getProcessID();
            int passes = cpuPasses.getOrDefault(pid, 0) + 1;
            cpuPasses.put(pid, passes);

            if(passes >= MAX_PASSES) {
                int rests = timesRested.getOrDefault(pid, 0) + 1;
                timesRested.put(pid, rests);

                if (rests >= MAX_RESTS) {
                    cpuPasses.put(pid, 0);
                    timesRested.put(pid, 0);
                    addToReady(executing, CPUTime);
                    newLog(executing, EventType.PREEMPTED, CPUTime);
                } else {
                    cpuPasses.put(pid, 0);
                    sendToRest(executing, CPUTime);
                    newLog(executing, EventType.PREEMPTED, CPUTime);
                }
            } else {
                addToReady(executing, CPUTime);
                newLog(executing, EventType.PREEMPTED, CPUTime);
            }

            executing = null;
            currentQuantum = 0;
        }
    }

    /**
     * @brief Atualiza o tempo de processos bloqueados e gere o retorno à fila.
     * @param CPUTime O tempo atual da CPU.
     */
    public void waitForIOEvent(int CPUTime){
        if(waiting == null){
            if(blocked.isEmpty()) return;
            waiting = blocked.removeFirst();
            waiting.setProcessState(ProcessState.WAITING);
        }

        waiting.waitForIO();

        if(waiting.IOHasArrived()) {
            waiting.resetIO();
            addToReady(waiting, CPUTime);
            newLog(waiting, EventType.UNBLOCKED, CPUTime);
            waiting = null;
        }
    }

    public List<Process> getFinishedProcesses(){ return this.finished; }

    public List<SchedulerEvent> getLog() { return log; }
}