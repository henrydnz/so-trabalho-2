/**
 * @brief Representa um processo no sistema operativo.
 */
public class Process {
    private final int processID;
    private final String processName;
    private final String processDescription;

    private final int systemArrivalTime;
    private final int CPUDuration;

    private int remainingTime;

    private final int priority;
    private final ProcessType type;

    private final boolean IOEvent;
    private final double IORequestProbability;
    private final double IORequestAverage;

    private final int IODuration;
    private int IORemainingTime;

    private ProcessState processState;

    private final int suggestedQueue;
    private final int suggestedQuantum;

    /**
     * @brief Construtor para inicializar os atributos do processo.
     * @param processID ID único do processo.
     * @param processName Nome do processo.
     * @param processDescription Descrição do comportamento do processo.
     * @param systemArrivalTime Tempo de chegada ao sistema.
     * @param CPUDuration Duração total necessária de CPU.
     * @param priority Prioridade base do processo.
     * @param type Tipo de processo (ProcessType).
     * @param IOEvent Indica se o processo faz operações de E/S.
     * @param IORequestProbability Probabilidade de solicitar E/S a cada ciclo.
     * @param IORequestAverage Média de pedidos de E/S.
     * @param IODuration Duração do bloqueio por E/S.
     * @param processState Estado inicial do processo.
     * @param suggestedQueue Fila sugerida (se aplicável).
     * @param suggestedQuantum Quantum sugerido (se aplicável).
     */
    public Process(int processID, String processName, String processDescription,
                   int systemArrivalTime, int CPUDuration, int priority, ProcessType type,
                   boolean IOEvent, double IORequestProbability, double IORequestAverage,
                   int IODuration, ProcessState processState, int suggestedQueue, int suggestedQuantum) {

        this.processID = processID;
        this.processName = processName;
        this.processDescription = processDescription;

        this.systemArrivalTime = systemArrivalTime;
        this.CPUDuration = CPUDuration;

        this.priority = priority;
        this.type = type;

        this.IOEvent = IOEvent;
        this.IORequestProbability = IORequestProbability;
        this.IORequestAverage = IORequestAverage;
        this.IODuration = IODuration;

        this.processState = processState;

        this.remainingTime = CPUDuration;
        this.IORemainingTime = IODuration;

        this.suggestedQueue = suggestedQueue;
        this.suggestedQuantum = suggestedQuantum;
    }

    /**
     * @brief Simula a execução do processo na CPU por um ciclo de relógio.
     */
    public void execute(){
        if(this.remainingTime > 0 && isExecuting()) this.remainingTime--;
    }

    /**
     * @brief Simula a espera por uma operação de E/S por um ciclo.
     */
    public void waitForIO(){
        if(this.IORemainingTime > 0 && isWaiting()) this.IORemainingTime--;
    }

    /**
     * @brief Verifica se o processo concluiu a sua execução de CPU.
     * @return Verdadeiro se o tempo restante for zero, falso caso contrário.
     */
    public boolean isDone(){ return this.remainingTime == 0; }

    /**
     * @brief Calcula aleatoriamente se o processo deve solicitar E/S neste ciclo.
     * @return Verdadeiro se o processo bloqueia para E/S, falso caso contrário.
     */
    public boolean requestIO(){ return Math.random() < this.IORequestProbability; }

    /**
     * @brief Verifica se o tempo necessário para a operação de E/S foi concluído.
     * @return Verdadeiro se a operação de E/S finalizou, falso caso contrário.
     */
    public boolean IOHasArrived(){ return this.IORemainingTime <= 0; }

    /**
     * @brief Reinicia o contador de tempo de operação de E/S para a sua duração padrão.
     */
    public void resetIO(){ this.IORemainingTime = this.IODuration; }

    // GETTERS
    public boolean isWaiting() { return this.processState == ProcessState.WAITING; }
    public boolean isBlocked(){ return this.processState == ProcessState.BLOCKED; }
    public boolean isReady(){ return this.processState == ProcessState.READY; }
    public boolean isExecuting() { return this.processState == ProcessState.EXECUTING; }
    public boolean isFinalized() { return this.processState == ProcessState.FINISHED; }

    public int getProcessID() { return processID; }
    public String getProcessName() { return processName; }
    public String getProcessDescription() { return processDescription; }

    public int getSystemArrivalTime() { return systemArrivalTime; }

    public int getCPUDuration() { return CPUDuration; }

    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }

    public int getPriority() { return priority; }

    public ProcessType getType() { return type; }

    public boolean hasIOEvent() { return IOEvent; }

    public double getIORequestProbability() { return IORequestProbability; }

    public double getIORequestAverage() { return IORequestAverage; }

    public int getIODuration() { return IODuration; }

    public int getIORemainingTime() { return IORemainingTime; }
    public void setIORemainingTime(int IORemainingTime) { this.IORemainingTime = IORemainingTime; }

    public ProcessState getProcessState() { return processState; }
    public void setProcessState(ProcessState processState) { this.processState = processState; }

    public int getSuggestedQueue() { return suggestedQueue; }

    public int getSuggestedQuantum() { return suggestedQuantum; }

    @Override
    public String toString() {
        return "Process{" +
                "processID=" + processID +
                ", processName='" + processName + '\'' +
                ", processDescription='" + processDescription + '\'' +
                ", systemArrivalTime=" + systemArrivalTime +
                ", CPUDuration=" + CPUDuration +
                ", remainingTime=" + remainingTime +
                ", priority=" + priority +
                ", type=" + type +
                ", IOEvent=" + IOEvent +
                ", IORequestProbability=" + IORequestProbability +
                ", IORequestAverage=" + IORequestAverage +
                ", IODuration=" + IODuration +
                ", IORemainingTime=" + IORemainingTime +
                ", processState=" + processState +
                ", suggestedQueue=" + suggestedQueue +
                ", suggestedQuantum=" + suggestedQuantum +
                '}';
    }
}
