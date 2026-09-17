public class Process {
    private int processID;
    private String processName;
    private String processDescription;

    private int systemArrivalTime;
    private int CPUDuration;

    private int remainingTime;

    private int priority;
    private ProcessType type;

    private boolean IOEvent;
    private double IORequestProbability;
    private int IORequestAverage;


    private int IODuration;
    private int IORemainingTime;

    private ProcessState processState;

    public Process(int processID, String processName, String processDescription,
                   int systemArrivalTime, int CPUDuration, int priority, ProcessType type,
                   boolean IOEvent, double IORequestProbability, int IORequestAverage,
                   int IODuration, ProcessState processState) {

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
    }

    public void execute(){
        if(this.remainingTime > 0 && isExecuting()) this.remainingTime--;
    }

    public void waitForIO(){
        if(this.IORemainingTime > 0 && isWaiting()) this.IORemainingTime--;
    }

    public boolean isDone(){
        return this.remainingTime == 0;
    }

    public boolean IOHasArrived(){
        return this.IORemainingTime == 0;
    }

    public void resetIO(){ this.IORemainingTime = this.IODuration; }

    public boolean isWaiting() { return this.processState == ProcessState.WAITING; }

    public boolean isBlocked(){
        return this.processState == ProcessState.BLOCKED;
    }

    public boolean isReady(){
        return this.processState == ProcessState.READY;
    }

    public boolean isExecuting() { return this.processState == ProcessState.EXECUTING; }

    public boolean isFinalized() { return this.processState == ProcessState.FINALIZED; }

    public boolean requestIO(){
        return Math.random() < this.IORequestProbability;
    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessDescription() {
        return processDescription;
    }

    public void setProcessDescription(String processDescription) {
        this.processDescription = processDescription;
    }

    public int getSystemArrivalTime() {
        return systemArrivalTime;
    }

    public void setSystemArrivalTime(int systemArrivalTime) {
        this.systemArrivalTime = systemArrivalTime;
    }

    public int getCPUDuration() {
        return CPUDuration;
    }

    public void setCPUDuration(int CPUDuration) {
        this.CPUDuration = CPUDuration;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }

    public boolean hasIOEvent() {
        return IOEvent;
    }

    public void setHasIOEvent(boolean IOEvent) {
        this.IOEvent = IOEvent;
    }

    public double getIORequestProbability() {
        return IORequestProbability;
    }

    public void setIORequestProbability(double IORequestProbability) {
        this.IORequestProbability = IORequestProbability;
    }

    public int getIORequestAverage() {
        return IORequestAverage;
    }

    public void setIORequestAverage(int IORequestAverage) {
        this.IORequestAverage = IORequestAverage;
    }

    public int getIODuration() {
        return IODuration;
    }

    public void setIODuration(int IODuration) {
        this.IODuration = IODuration;
    }

    public int getIORemainingTime() {
        return IORemainingTime;
    }

    public void setIORemainingTime(int IORemainingTime) {
        this.IORemainingTime = IORemainingTime;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }


}
