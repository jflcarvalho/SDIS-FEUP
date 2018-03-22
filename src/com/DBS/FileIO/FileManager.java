package com.DBS.FileIO;

import com.DBS.Chunk;
import com.Network.Peer.Peer;

import java.io.*;

public class FileManager {
    public static int createFile(Chunk chunk, Peer peer){
        //Creates sub folders structure -> peerId/FileId/
        String file_path = "backup/" + peer.getPeerID() + "/" + chunk.getFileID() + "/" + chunk.getChunkID();
        try {
            File outFile = new File(file_path);
            if(!outFile.getParentFile().mkdirs())
                return 1;
            if(!outFile.createNewFile())
                return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public static int writeFile(Chunk chunk, Peer peer) {
        String file_path = "backup/" + peer.getPeerID() + "/" + chunk.getFileID() + "/" + chunk.getChunkID();
        OutputStream output;
        try {
            output = new FileOutputStream(file_path);
            output.write(chunk.getData(), 0, chunk.getData().length);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}
