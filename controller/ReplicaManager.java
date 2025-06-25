package controller;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import java.io.IOException;

//Interfaces with HDFS to set replication factors
//This is where ADRS “writes back” to HDFS and makes actual changes
public class ReplicaManager {
    private final FileSystem fs;

    public ReplicaManager() throws IOException {
        Configuration conf = new Configuration();
        this.fs = FileSystem.get(conf);
    }

    //
    public void setReplication(String pathStr, short replication) throws IOException {
        Path path = new Path(pathStr);
        //Uses Hadoop’s native API: fs.setReplication(path, replication)
        //Requires Hadoop environment and configuration to run
        fs.setReplication(path, replication);
    }
}