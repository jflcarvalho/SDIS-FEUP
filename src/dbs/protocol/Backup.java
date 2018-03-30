package dbs.protocol;

import dbs.Chunk;
import dbs.file_io.M_File;
import dbs.message.MessageFactory;
import dbs.message.PutChunkMessage;
import dbs.message.StoredMessage;
import dbs.peer.Peer;

import java.io.File;

import static dbs.file_io.FileManager.createFile;
import static dbs.file_io.FileManager.writeFile;
import static dbs.utils.Constants.DEBUG;
import static dbs.utils.Constants.NUMBER_OF_TRIES;
import static dbs.utils.Constants.SLEEP_TIME;

public class Backup implements Runnable {
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
        System.out.println("\n------------------ END ---------------------\n");
    }

    /**
     * Sends PUTCHUNK request for the multicast backup channel (MDB) with the following format:
     * PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
     * Then waits one second, in first try, and checks if the desired replication degree
     * has been accomplished. Otherwise it resend the PUTCHUNK request, and in this tries
     * waits the double of the first time, until a maximum of 5 tries.
     * @param chunk
     */
    private void sendChunk(Chunk chunk) {
        PutChunkMessage message = MessageFactory.getPutChunkMessage(this, chunk);
        peer.updateReplicationDatabase(message);
        int tries = 0;
        int degree;
        do {
            peer.send(message);
            try {
                //Waiting time is double if have fail in first time
                Thread.sleep((long) (SLEEP_TIME * (tries < 1 ? 1 : 2)));
            } catch (InterruptedException e) {
                if(DEBUG)
                    e.printStackTrace();
                else
                    System.out.println("[ERROR] Sleeping Thread");
            }
            degree = peer.getActualRepDegree(mFile.getFileID(), chunk.getChunkID());
            tries++;
        }while (tries < NUMBER_OF_TRIES && degree < replication_degree);

        System.out.print("Chunk nº: " + chunk.getChunkID());
        if(tries <= NUMBER_OF_TRIES && degree >= replication_degree)
            System.out.print(" Stored with Success\n");
        else
            System.out.print(" Unsuccessful\n");
    }

    /**
     * Chunks stored in path like this:
     * ./backup/<peedID>/<fileID>/<chunkNO>
     * @param chunk
     */
    public void storeChunk(Chunk chunk) {
        String peerID = getPeerID();
        StoredMessage message = MessageFactory.getStoredMessage(peerID, chunk);
        if(peer.haveChunk(chunk)){
            sendStored(message);
            return;
        }
        String file_path = "backup/" + peerID + "/" + chunk.getFileID() + "/" + chunk.getChunkID();
        if(createFile(file_path)){
            if (!writeFile(chunk, file_path))
                return;
        }
        long file_Size = (new File(file_path)).length();
        peer.addChunk(chunk, file_Size);
        sendStored(message);
    }

    public Chunk readChunk(){
        return null;
    }

    private void sendStored(StoredMessage message){
        peer.addReplicationDatabase(message);
        try {
            Thread.sleep((long) (Math.random() * 400));
        } catch (InterruptedException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Sleeping Thread");
        }
        peer.send(message);
    }

    private String getPeerID(){
        return peer.getPeerID();
    }

}
