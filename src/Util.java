import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * @brief Classe utilitária estática para geração de relatórios e registos.
 */
public class Util {

    /**
     * @brief Processa os logs e escreve relatórios e gráficos nos ficheiros de saída.
     * @param log Lista de eventos gerados durante a simulação.
     * @param processes Lista de processos.
     * @param algName Nome/sigla do algoritmo testado (usado no nome do ficheiro).
     */
    public static void getLog(List<SchedulerEvent> log, List<Process> processes, String algName) {
        StatisticsCalculator calculator = new StatisticsCalculator();
        SimulationSummary summary = calculator.calculate(log, processes);
        List<GanttEntry> gantt = calculator.buildGanttChart(log, processes);

        try (PrintStream fileOut = new PrintStream(new FileOutputStream("report_" + algName + ".txt"))) {
            summary.printReportFile(fileOut);
        } catch (IOException e) { throw new RuntimeException(e); }

        try (PrintStream fileOut = new PrintStream(new FileOutputStream("gantt_" + algName + ".txt"))) {
            summary.printGanttChartFile(gantt, fileOut);
        } catch (IOException e) { throw new RuntimeException(e); }
    }
}
