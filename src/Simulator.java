import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private List<Process> processes;
    private int processCount;

    private Process executing;

    private int totalCPUTime;

    public Simulator() {
        this.processes = new ArrayList<Process>();
        this.totalCPUTime = 0;
    }

    public void readProcessList(String filename) throws RuntimeException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // descarta header do csv
            String header = reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                // field_index header_csv:
                // 0 pid,
                // 1 nome_processo,
                // 2 tempo_chegada,
                // 3 tempo_cpu_total,
                // 4 prioridade,
                // 5 tipo_processo,
                // 6 operacao_es,
                // 7 probabilidade_es,
                // 8 media_es,
                // 9 duracao_es,
                // 10 fila_sugerida,
                // 11 quantum_sugerido,
                // 12 descricao

                int processID = Integer.parseInt(fields[0].trim().substring(1));
                String processName =  fields[1].trim();
                int processSystemArriveTime = Integer.parseInt(fields[2].trim());
                int processCPUTime = Integer.parseInt(fields[3].trim());
                int priority = Integer.parseInt(fields[4].trim());


                // pega o resto dos fields de acordo com o csv
                // processando cada um de acordo com o tipo
                // depois cria o objeto de process com os dados e coloca na lista de processos
                // atualiza o process count
            }

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


    public void runRoundRobin(){
        RoundRobin roundRobin = new RoundRobin(processes, 4);

        while(roundRobin.getFinalizedProcessCount() < processCount){
            this.totalCPUTime++;
            roundRobin.updateExecutingProcess();

            roundRobin.waitForIOEvent();
        }

        int time = roundRobin.getTotalCPUTime();
    }

    public void runMultilevelQueue(){

    }

    //    public void runNewAlg(){
    //
    //    }
}
