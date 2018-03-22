package com.DBS.Protocol;

import com.DBS.Chunk;

public class Backup implements Protocol {

    private String file_path;
    private int replication_degree;

    public Backup(String file_path, int replication_degree) {
        this.file_path = file_path;
        this.replication_degree = replication_degree;
    }

    public void run() {
      System.out.println("Backing up " + file_path);
      System.out.println("Replication degree: " + replication_degree);
    }

    public void sendChunk(Chunk chunk) {

    }

    public void storeChunk(Chunk chunk) {

    }
}
