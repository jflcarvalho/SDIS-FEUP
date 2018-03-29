package dbs.protocol;

import dbs.Chunk;
import dbs.file_io.M_File;
import dbs.message.Message;
import dbs.peer.Peer;

import java.io.File;

import static dbs.file_io.FileManager.createFile;
import static dbs.file_io.FileManager.writeFile;
import static dbs.utils.Constants.MessageType.PUTCHUNK;

public class Backup implements Protocol {

    private String file_path;
    private int replication_degree;
    private M_File mFile;
    private Peer peer;

    public Backup(String file_path, int replication_degree, Peer peer) {
        this.file_path = file_path;
        this.replication_degree = replication_degree;
        this.peer = peer;
    }

    public void run() {
        System.out.println("\n----------------- BACKUP --------------------\n");
        System.out.println("Backing up " + file_path);
        System.out.println("Replication degree: " + replication_degree + "\n");
        mFile = new M_File(file_path);
        System.out.println(mFile.toString());
        for(int i = 0; i < mFile.getChunks().length; i++){
            sendChunk(mFile.getChunks()[i]);
        }
        System.out.println("\n------------------ END ---------------------\n");
    }

    private void sendChunk(Chunk chunk) {
        Message message = new Message(PUTCHUNK, 1, peer.getPeerID(), chunk.getFileID(), chunk.getChunkID(), replication_degree, chunk.getData());
        peer.send(message);
    }

    public void storeChunk(Chunk chunk) {
        createFile(chunk, peer);
        writeFile(chunk, peer);
        String file_path = "backup/" + peer.getPeerID() + "/" + chunk.getFileID() + "/" + chunk.getChunkID();
        long file_Size = (new File(file_path)).length();
        peer.addChunk(chunk, file_Size);
    }

    public Chunk readChunk(){
        return null;
    }
}
