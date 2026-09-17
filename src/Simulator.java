import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private final List<Process> processes;
    private int processCount;

    private int CPUTime;

    public Simulator() {
        this.processes = new ArrayList<Process>();

        this.CPUTime = 0;
    }

    public ProcessType parseProcessType(String type){
        switch (type) {
            case "tempo_real" -> {
                return ProcessType.REAL_TIME;
            }
            case "interativo" -> {
                return ProcessType.INTERACTIVE;
            }
            case "io_bound" -> {
                return ProcessType.IO_BOUND;
            }
            case "misto" -> {
                return ProcessType.MIXED;
            }
            case "cpu_bound" -> {
                return ProcessType.CPU_BOUND;
            }
            case "batch" -> {
                return ProcessType.BATCH;
            }
            default -> {
                return null;
            }
        }
    }

    public void readProcessList(String filename) throws RuntimeException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // descarta header do csv
            String header = reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                int pid = Integer.parseInt(fields[0].trim().substring(1));
                String name = fields[1].trim();
                int arrivalTime = Integer.parseInt(fields[2].trim());
                int CPUDuration = Integer.parseInt(fields[3].trim());
                int priority = Integer.parseInt(fields[4].trim());
                ProcessType processType = parseProcessType(fields[5].trim());
                boolean hasIO = Integer.parseInt(fields[6].trim()) == 1;
                double IOProbability = Double.parseDouble(fields[7].trim());
                double IOAverage = Double.parseDouble(fields[8].trim());
                int IODuration = Integer.parseInt(fields[9].trim());
                ProcessState processState = ProcessState.READY;
                int suggestedQueue = Integer.parseInt(fields[10].trim());
                int suggestedQuantum = Integer.parseInt(fields[11].trim());
                String description = fields[12].trim();

                this.processes.add(new Process(
                        pid, name, description, arrivalTime,
                        CPUDuration, priority, processType, hasIO, IOProbability,
                        IOAverage, IODuration, processState, suggestedQueue, suggestedQuantum
                ));
            }

            this.processCount = this.processes.size();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // escolher opcao de escalonador
    // 1 - round robin
    // 2 - multiplas filas
    // 3 - metodo proposto pelo grupo
    public void runSimulation(int option){
        if(option == 0){
            runRoundRobin();
        } else if (option == 1){
            runMultilevelQueue();
        } else if(option == 2){
            // runNewAlg
        }
    }

    private void addNewProcesses(List<Process> ps, RoundRobin rr){
        for(int i = 0; i < ps.size(); i++){
            Process process = ps.get(i);
            if(process.getSystemArrivalTime() <= this.CPUTime){
                rr.addReadyProcess(process);
            }
        }
    }

    public void runRoundRobin(){
        RoundRobin roundRobin = new RoundRobin(4);

        while(roundRobin.getFinishedProcesses().size() != processCount) {

            addNewProcesses(this.processes, roundRobin);

            this.CPUTime++;

            roundRobin.updateExecutingProcess(this.CPUTime);
            roundRobin.waitForIOEvent(this.CPUTime);
        }

        System.out.println("finished processes:");
        for(Process p : roundRobin.getFinishedProcesses()){
            System.out.print("p"+p.getProcessID()+", ");
        }
    }

    public void runMultilevelQueue(){

    }

    public void showProcesses(){
        for(Process p : processes)
            System.out.println(p);
    }

    public int getCPUTime() {
        return CPUTime;
    }

    //    public void runNewAlg(){
    //
    //    }
}
