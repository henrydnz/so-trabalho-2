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

    public void execute(){
        if(this.remainingTime > 0 && isExecuting()) this.remainingTime--;
    }

    public void waitForIO(){
        if(this.IORemainingTime > 0 && isWaiting()) this.IORemainingTime--;
    }

    public boolean isDone(){ return this.remainingTime == 0; }

    public boolean requestIO(){ return Math.random() < this.IORequestProbability; }

    public boolean IOHasArrived(){ return this.IORemainingTime <= 0; }

    public void resetIO(){ this.IORemainingTime = this.IODuration; }

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
