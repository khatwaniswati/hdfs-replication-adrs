package evaluator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//tracks operation time and logs outcomes
public class Evaluator {

    private static PrintWriter csvWriter;

    static {
        try {
            csvWriter = new PrintWriter(new FileWriter("/app/output/replication_log.csv"));
            csvWriter.println("timestamp,filename,oldReplication,newReplication,heatScore");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void logReplicationEvent(String file, int oldRep, int newRep, double heat) {
        long timestamp = System.currentTimeMillis();
        csvWriter.printf("%d,%s,%d,%d,%.2f\n", timestamp, file, oldRep, newRep, heat);
        csvWriter.flush();
    }

    //measures time in ms
    public static long timeOperation(Runnable operation) {
        long start = System.nanoTime();
        operation.run();
        long end = System.nanoTime();
        return (end - start) / 1_000_000; // return ms
    }

    //standardized log messages
    public static void log(String message) {
        System.out.println("[EVAL] " + message);
    }


    public static void logToFile(String message) {
        try (FileWriter fw = new FileWriter("replication_log.txt", true)) {
            fw.write("[EVAL] " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeLogger() {
        if (csvWriter != null) csvWriter.close();
    }

}