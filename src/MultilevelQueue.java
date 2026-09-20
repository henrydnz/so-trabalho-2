
import java.util.ArrayList;
import java.util.List;

public class MultilevelQueue {
    private final RoundRobin queue01;
    private final RoundRobin queue02;
    private final RoundRobin queue03;

    public MultilevelQueue(int quantum01, int quantum02, int quantum03) {
        this.queue01 = new RoundRobin(quantum01);
        this.queue02 = new RoundRobin(quantum02);
        this.queue03 = new RoundRobin(quantum03);
    }

    public void addProcess(Process process){
        ProcessType pt = process.getType();
        if(pt == ProcessType.REAL_TIME || pt == ProcessType.INTERACTIVE)
            queue01.addReadyProcess(process);

        if(pt == ProcessType.IO_BOUND || pt == ProcessType.MIXED)
            queue02.addReadyProcess(process);

        if(pt == ProcessType.CPU_BOUND || pt == ProcessType.BATCH)
            queue03.addReadyProcess(process);
    }

    public void execute(int CPUTime, int processCount){
        //
    }
}
