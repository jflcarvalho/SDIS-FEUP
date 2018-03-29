package dbs.protocol;

import dbs.Chunk;
import dbs.file_io.M_File;
import dbs.message.Message;
import dbs.message.MessageFactory;
import dbs.peer.Peer;

import java.io.File;

import static dbs.file_io.FileManager.createFile;
import static dbs.file_io.FileManager.writeFile;
import static dbs.utils.Constants.SLEEP_TIME;

public class Backup implements Protocol {
    public final static double VERSION = 1.0;

    private String file_path;
    private int replication_degree;
    private M_File mFile;
    private Peer peer;

    public Backup(Peer peer){
        this.peer = peer;
    }

    public Backup(String file_path, int replication_degree, Peer peer) {
        this.file_path = file_path;
        this.replication_degree = replication_degree;
        this.peer = peer;
    }

    public Peer getPeer() {
        return peer;
    }

    public int getReplicationDegree() {
        return replication_degree;
    }

    public void run() {
        System.out.println("\n----------------- BACKUP --------------------\n");
        System.out.println("Backing up " + file_path);
        System.out.println("Replication degree: " + replication_degree + "\n");
        mFile = new M_File(file_path);
        System.out.println(mFile.toString());
        for(Chunk chunk : mFile.getChunks())
            new Thread(() -> sendChunk(chunk)).start();
            //sendChunk(chunk);
        System.out.println("\n------------------ END ---------------------\n");
    }

    private void sendChunk(Chunk chunk) {
        Message message = MessageFactory.getPutChunkMessage(this, chunk);
        int tries = 0;
        int degree;
        do {
            peer.send(message);
            try {
                Thread.sleep((long) (SLEEP_TIME));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            degree = peer.getDegree(message.getFileID(), message.getChunkNO());
            tries++;

            System.out.println("Chunk nº: " + chunk.getChunkID());
            System.out.println("Try nº: " + tries);
            System.out.println("RepDeg: " + degree);
        }while (tries < 3 && degree < replication_degree);
    }

    public void storeChunk(Chunk chunk) {
        if(peer.haveChunk(chunk)){
            peer.send(MessageFactory.getStoredMessage(peer.getPeerID(), chunk));
            return;
        }
        String peerID = peer.getPeerID();
        String file_path = "backup/" + peerID + "/" + chunk.getFileID() + "/" + chunk.getChunkID();
        if(createFile(file_path)){
            if (!writeFile(chunk, file_path))
                return;
        }
        long file_Size = (new File(file_path)).length();
        peer.addChunk(chunk, file_Size);
    }

    public Chunk readChunk(){
        return null;
    }

}
