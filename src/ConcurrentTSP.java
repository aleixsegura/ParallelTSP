/* ---------------------------------------------------------------
Práctica 2.
Código fuente: ConcurrentTSP.java
Grau Informàtica
Aleix Segura Paz.
Aniol Serrano Ortega.
--------------------------------------------------------------- */
import jdk.jshell.spi.ExecutionControl;

import java.util.concurrent.ExecutionException;

public class ConcurrentTSP {

    public static void main(String[] args) throws ExecutionControl.NotImplementedException {
        TSP tsp = null;
        String citiesFile;
        int numberOfThreads;
        String concurrentMethod = null;

        if (args.length < 1 || args.length > 3) exitWithUsage();

        else if (args.length == 1){
            concurrentMethod = args[0];
            tsp = new TSP(concurrentMethod);
        }
        else if (args.length == 2) {
            citiesFile = args[0];
            concurrentMethod = args[1];
            tsp = new TSP(citiesFile, concurrentMethod);
        }
        else{
            citiesFile = args[0];
            numberOfThreads = Integer.parseInt(args[1]);
            concurrentMethod = args[2];
            tsp = new TSP(citiesFile, numberOfThreads, concurrentMethod);
        }

        assert concurrentMethod != null;
        if (concurrentMethod.equals("ForkJoinPool")) throw new ExecutionControl.NotImplementedException("" +
                "This part hasn't been implemented.");

        tsp.solve();
    }

    /**
     * Finalizes program after displaying the valid usage with its parameters.
     */
    private static void exitWithUsage(){
        System.err.println("""
                Error in Parameters. Usage: ConcurrentTSP [<Cities_File>] [<Num_Threads>] [<Concurrent_Method>] or\s
                  \t\t\t\t\t\t\tConcurrentTSP [<Cities_File>] [<Concurrent_Method>] or\s
                 \t\t\t\t\t\t\tConcurrentTSP [<Concurrent_Method>]""");
        System.exit(1);
    }
}
