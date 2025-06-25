package tracker;

import java.util.*;

//Class which tracks and computes heat scores based on access frequency and recency
//Used to decide which files are hot/warm/cold
public class AccessTracker {

    //static class to record lastAccessTime for the file and
    // increment it's frequency while accessing it
    private static class AccessStats {
        int frequency;
        long lastAccessTime;

        public void increment() {
            frequency++;
            lastAccessTime = System.currentTimeMillis();
        }

        public double getHeat() {
            long age = System.currentTimeMillis() - lastAccessTime;
            return frequency * Math.exp(-age / 60000.0); // decay over time
        }
    }

    //variable to store filename along with path and it's access frequency
    private final Map<String, AccessStats> accessMap = new HashMap<>();

    public void recordAccess(String filePath) {
        accessMap.computeIfAbsent(filePath, k -> new AccessStats()).increment();
    }


    //time-decay function — recent accesses count more
    public double getHeat(String filePath) {
        return accessMap.getOrDefault(filePath, new AccessStats()).getHeat();
    }

    //Returns a map of file paths and their heat values.
    public Map<String, Double> getAllHeatScores() {
        Map<String, Double> scores = new HashMap<>();
        for (String path : accessMap.keySet()) {
            scores.put(path, getHeat(path));
        }
        return scores;
    }
} 