package main;

import controller.*;
import tracker.*;
import predictor.*;
import evaluator.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//Orchestrates everything — simulates access, triggers prediction, applies replication
//connects everything and would eventually take real logs or streaming access data as input
public class Main {

    public static void main(String[] args) throws IOException {
        AccessTracker tracker = new AccessTracker();
        HysteresisController hysteresis = new HysteresisController();
        ReplicaManager replicaManager = new ReplicaManager();
        MarkovPredictor predictor = new MarkovPredictor();

        System.out.println("\n===== Adaptive Replication (ADRS) - Simulation Started =====\n");

        Set<String> uniqueFiles = new HashSet<>();

        // === Step 1: Read real log from CSV ===
        BufferedReader br = new BufferedReader(new FileReader("hdfs_access_log.csv"));
        String line;
        boolean first = true;

        while ((line = br.readLine()) != null) {
            if (first) { first = false; continue; }  // Skip header

            String[] parts = line.split(",");
            if (parts.length < 2) continue;

            String filePath = parts[1].trim();
            tracker.recordAccess(filePath);
            uniqueFiles.add(filePath);

            // Optional: use timestamp if needed
            // String ts = parts[0];
        }

        // === Step 2: Simulate Prediction Observations ===
        List<String> observed = new ArrayList<>(uniqueFiles);
        for (int i = 0; i < observed.size() - 1; i++) {
            predictor.observe(observed.get(i), observed.get(i + 1));
        }

        // === Step 3: Apply replication strategy ===
        for (String file : uniqueFiles) {
            if (!file.startsWith("/")) continue;

            // Clean file name
            String cleanPath = file
                    .split("\\?")[0]
                    .replaceAll("[^a-zA-Z0-9/_\\-.]", "_")
                    .replaceAll("^/+", "/");

            if (cleanPath.trim().isEmpty() || cleanPath.equals("/") || cleanPath.startsWith("//")) {
                Evaluator.log("Skipping invalid path: " + cleanPath);
                continue;
            }

            double heat = tracker.getHeat(file);

            int oldRep = replicaManager.getCurrentReplication(cleanPath);
            int newRep = hysteresis.adjustReplication(heat,oldRep);
            Evaluator.log("Heat::"+heat+" oldRep:"+oldRep+" newRep:"+newRep);

            if (oldRep != newRep) {
                replicaManager.setReplication(cleanPath, newRep);
                Evaluator.log("Adjusted replication for >> " + cleanPath + " to " + newRep);
                Evaluator.logReplicationEvent(cleanPath, oldRep, newRep, heat);
            } else {
                Evaluator.log("No change for " + cleanPath);
            }

            String next = predictor.predictNext(file);
            Evaluator.log("Predicted next access after " + file + ": " + next);
        }

        br.close();

    }

//    public void compareMyLogs(){
//        Map<String, List<Double>> heatTimeline = new HashMap<>();
//        Map<String, List<Integer>> repTimeline = new HashMap<>();
//
//        for (int t = 0; t < 5; t++) {
//            for (String file : files) {
//                tracker.recordAccess(file); // Simulate access
//                double heat = tracker.getHeat(file);
//                int currentRep = 2;
//                int newRep = hysteresis.adjustReplication(heat, currentRep);
//
//                // Log heat and replication
//                heatTimeline.computeIfAbsent(file, k -> new ArrayList<>()).add(heat);
//                repTimeline.computeIfAbsent(file, k -> new ArrayList<>()).add(newRep);
//            }
//
//            // Simulate time delay (for decay)
//            Thread.sleep(1000); // Optional, for realism
//        }
//    }
}
