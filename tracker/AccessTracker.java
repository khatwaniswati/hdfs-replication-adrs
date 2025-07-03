package tracker;

import java.util.*;

//Class which tracks and computes heat scores based on access frequency and recency
//Used to decide which files are hot/warm/cold
public class AccessTracker {


    private Map<String, Integer> accessCounts = new HashMap<>();

    public void recordAccess(String file) {
        accessCounts.put(file, accessCounts.getOrDefault(file, 0) + 1);
    }

    public double getHeat(String file) {
        int count = accessCounts.getOrDefault(file, 0);
        int max = accessCounts.values().stream().max(Integer::compare).orElse(1);
        return Math.log(1 + count) * 200.0 / Math.log(1 + max);  // scale adjusted
    }

} 