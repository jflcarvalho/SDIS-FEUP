package com.DBS.Protocol;

import com.DBS.Chunk;
import com.DBS.FileIO.M_File;
import com.Network.Peer.Peer;

import java.io.File;

import static com.DBS.FileIO.FileManager.createFile;
import static com.DBS.FileIO.FileManager.writeFile;

public class Backup implements Protocol {

    private String file_path;
    private int replication_degree;
    private M_File mFile;
    private Peer peer;

    public Backup(String file_path, int replication_degree) {
        this.file_path = file_path;
        this.replication_degree = replication_degree;
        mFile = new M_File(file_path);
    }

    public void run() {
      System.out.println("Backing up " + file_path);
      System.out.println("Replication degree: " + replication_degree);
    }

    public void sendChunk(Chunk chunk) {

    }

    public void storeChunk(Chunk chunk) {
        createFile(chunk, peer);
        writeFile(chunk, peer);
        String file_path = "backup/" + peer.getPeerID() + "/" + chunk.getFileID() + "/" + chunk.getChunkID();
        long file_Size = (new File(file_path)).length();
        peer.addChunk(chunk, file_Size);
    }
}
