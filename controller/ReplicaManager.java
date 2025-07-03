package controller;

import evaluator.Evaluator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//Interfaces with HDFS to set replication factors
//This is where ADRS “writes back” to HDFS and makes actual changes
public class ReplicaManager {

    private Map<String, Integer> replicationMap = new HashMap<>();

    private final FileSystem fs;

    public ReplicaManager() throws IOException {
        Configuration conf = new Configuration();
        this.fs = FileSystem.get(conf);
    }

    public int getCurrentReplication(String file) {
        return replicationMap.getOrDefault(file, 3); // Default to 3
    }

    public void setReplication(String file, int newReplication) {
        int oldReplication = getCurrentReplication(file);
        replicationMap.put(file, newReplication);

        Evaluator.log("[REPLICATE] " + file +
                " | old: " + oldReplication +
                " → new: " + newReplication);
    }

    public void setReplication(String pathStr, short replication) throws IOException {
        Path path = new Path(pathStr);
        //Uses Hadoop’s native API: fs.setReplication(path, replication)
        //Requires Hadoop environment and configuration to run
        fs.setReplication(path, replication);
    }
}