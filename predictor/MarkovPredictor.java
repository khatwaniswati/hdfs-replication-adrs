package predictor;

import java.util.*;

//Learns transitions between file accesses to predict the next most probable file access
//Allows ADRS to preemptively replicate files before they become hot — improves responsiveness
public class MarkovPredictor {
    private final Map<String, Map<String, Integer>> transitions = new HashMap<>();

    //method that builds transition counts
    public void observe(String from, String to) {
        transitions
            .computeIfAbsent(from, k -> new HashMap<>())
            .merge(to, 1, Integer::sum);
    }

    //predicts next file based on max probability
    public String predictNext(String current) {
        Map<String, Integer> next = transitions.getOrDefault(current, new HashMap<>());
        return next.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(current);
    }
}