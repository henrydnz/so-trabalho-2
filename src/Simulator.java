import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Classe responsável pela gestão do ambiente de simulação e leitura de dados.
 */
public class Simulator {
    private final List<Process> processes;
    private int processCount;

    private int CPUTime;

    public Simulator() {
        this.processes = new ArrayList<Process>();

        this.CPUTime = 0;
    }

    /**
     * @brief Converte uma string no tipo de processo correspondente.
     * @param type Texto que descreve o tipo no ficheiro.
     * @return O tipo de processo em formato ProcessType.
     */
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

    /**
     * @brief Lê uma lista de processos a partir de um arquivo CSV.
     * @param filename O nome/caminho do arquivo a ser lido.
     * @throws RuntimeException Exceção lançada se houver erro de E/S.
     */
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

    /**
     * @brief Inicia a simulação usando o algoritmo escolhido.
     * @param option (0) RoundRobin, (1) MultilevelQueue, (2) StaminaRoundRobin.
     */
    public void runSimulation(int option){
        if(option == 0){
            runRoundRobin();
        } else if (option == 1){
            runMultilevelQueue();
        } else if(option == 2){
            runStaminaRoundRobin();
        }
    }

    /**
     * @brief Adiciona novos processos que chegaram no tempo atual à fila do escalonador Round Robin.
     * @param ps A lista de todos os processos do sistema.
     * @param rr A instância do escalonador Round Robin.
     * @param lastProcessAddedIndex O índice do último processo que foi adicionado à fila.
     * @return O novo índice atualizado do último processo adicionado.
     */
    private int addNewProcessesRR(List<Process> ps, RoundRobin rr, int lastProcessAddedIndex){
        for (int i = lastProcessAddedIndex; i < ps.size(); i++) {
            Process process = ps.get(i);
            if(process.getSystemArrivalTime() == this.CPUTime) {
                rr.addReadyProcess(process, this.CPUTime);
                lastProcessAddedIndex = i;
            }
        }

        return lastProcessAddedIndex;
    }

    /**
     * @brief Executa a simulação apenas com o escalonador Round Robin.
     */
    public void runRoundRobin(){
        this.CPUTime = 0;
        int lastProcessAddedIndex = 0;
        RoundRobin roundRobin = new RoundRobin(4);

        while(roundRobin.getFinishedProcesses().size() != processCount) {
            lastProcessAddedIndex = addNewProcessesRR(this.processes, roundRobin, lastProcessAddedIndex);
            this.CPUTime++;
            roundRobin.updateExecutingProcess(this.CPUTime);
            roundRobin.waitForIOEvent(this.CPUTime);
        }

        Util.getLog(roundRobin.getLog(), this.processes, "RR");
    }

    /**
     * @brief Adiciona novos processos que chegaram no tempo atual à fila do escalonador Multilevel Queue.
     * @param ps A lista de todos os processos do sistema.
     * @param mlq A instância do escalonador de Múltiplas Filas.
     * @param lastProcessAddedIndex O índice do último processo que foi adicionado à fila.
     * @return O novo índice atualizado do último processo adicionado.
     */
    private int addNewProcessesMLQ(List<Process> ps, MultilevelQueue mlq, int lastProcessAddedIndex){
        for (int i = lastProcessAddedIndex; i < ps.size(); i++) {
            Process process = ps.get(i);
            if (process.getSystemArrivalTime() == this.CPUTime) {
                mlq.addProcess(process, this.CPUTime);
                lastProcessAddedIndex = i;
            }
        }
        return lastProcessAddedIndex;
    }

    /**
     * @brief Executa a simulação apenas com o escalonador Multilevel Queue.
     */
    public void runMultilevelQueue(){
        this.CPUTime = 0;
        int lastProcessAddedIndex = 0;
        MultilevelQueue mlq = new MultilevelQueue(2, 4, 8);

        while (mlq.getFinishedCount() != processCount) {
            lastProcessAddedIndex = addNewProcessesMLQ(this.processes, mlq, lastProcessAddedIndex);
            this.CPUTime++;
            mlq.execute(this.CPUTime);
        }

        Util.getLog(mlq.getCombinedLog(), this.processes, "MLQ");
    }

    /**
     * @brief Adiciona novos processos que chegaram no tempo atual à fila do escalonador Stamina Round Robin.
     * @param ps A lista de todos os processos do sistema.
     * @param srr A instância do escalonador Stamina Round Robin.
     * @param lastProcessAddedIndex O índice do último processo que foi adicionado à fila.
     * @return O novo índice atualizado do último processo adicionado.
     */
    private int addNewProcessesStamina(List<Process> ps, StaminaRoundRobin srr, int lastProcessAddedIndex){
        for (int i = lastProcessAddedIndex; i < ps.size(); i++) {
            Process process = ps.get(i);
            // Verifica se o tempo de chegada do processo corresponde ao relógio atual da simulação
            if(process.getSystemArrivalTime() == this.CPUTime) {
                srr.addToReady(process, this.CPUTime);
                lastProcessAddedIndex = i;
            }
        }
        return lastProcessAddedIndex;
    }

    /**
     * @brief Executa a simulação apenas com o algoritmo Stamina Round Robin.
     */
    public void runStaminaRoundRobin(){
        this.CPUTime = 0; // Reinicia o relógio global
        int lastProcessAddedIndex = 0;

        // Inicializa o StaminaRoundRobin com um quantum (ex: 4, igual ao RR original)
        StaminaRoundRobin staminaRR = new StaminaRoundRobin(4);

        // O loop continua até que a lista de finalizados tenha todos os processos
        while(staminaRR.getFinishedProcesses().size() != processCount) {

            // 1. Adiciona novos processos que chegaram neste milissegundo
            lastProcessAddedIndex = addNewProcessesStamina(this.processes, staminaRR, lastProcessAddedIndex);

            // 2. Avança o relógio do sistema
            this.CPUTime++;

            // 3. Atualiza quem está na CPU (e quem está a descansar na fila de rest)
            staminaRR.updateExecutingProcess(this.CPUTime);

            // 4. Atualiza os processos que estão bloqueados à espera de I/O
            staminaRR.waitForIOEvent(this.CPUTime);
        }

        // Quando todos terminarem, imprime os relatórios estatísticos e o Gráfico de Gantt
        Util.getLog(staminaRR.getLog(), this.processes, "SRR");
    }

    public void resetProcesses(){ this.processes.clear(); this.processCount = 0; }

    public int getCPUTime() { return CPUTime; }

}
