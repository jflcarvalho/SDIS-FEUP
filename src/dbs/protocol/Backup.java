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
import static dbs.utils.Constants.NUMBER_OF_TRIES;
import static dbs.utils.Constants.SLEEP_TIME;
import static dbs.utils.Utils.sleepRandomTime;
import static dbs.utils.Utils.sleepThread;

public class Backup implements Runnable {
    public final static double VERSION = 1.0;

    private String file_path;
    private int replication_degree;
    private M_File mFile;
    private final Peer peer;

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
        //writeFile(mFile.getData(), "restore/" + peer.getPeerID() + "/" + "original_" + mFile.getName());
        System.out.println(mFile.toString());
        for(Chunk chunk : mFile.getChunks())
            new Thread(() -> sendChunk(chunk, replication_degree)).start();
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
    void sendChunk(Chunk chunk, int repDeg) {
        PutChunkMessage message = MessageFactory.getPutChunkMessage(peer.getPeerID(), chunk, repDeg);
        peer.updateReplicationDatabase(message);
        int tries = 0;
        int degree;
        do {
            peer.send(message);
            sleepThread(SLEEP_TIME * (tries < 1 ? 1 : 2));
            degree = peer.getActualRepDegree(chunk.getFileID(), chunk.getChunkID());
            tries++;
        }while (tries < NUMBER_OF_TRIES && degree < repDeg);

        System.out.print("Chunk nº: " + chunk.getChunkID());
        if(tries <= NUMBER_OF_TRIES && degree >= repDeg)
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
        String peerID = getPeerID(), fileID = chunk.getFileID();
        int chunkID = chunk.getChunkID();
        StoredMessage message = MessageFactory.getStoredMessage(peerID, chunk);
        if(peer.haveChunk(fileID, chunkID)){
            sendStored(message);
            return;
        }
        String file_path = "backup/" + peerID + "/" + fileID + "/" + chunkID;
        if(createFile(file_path)){
            if (!writeFile(chunk.getData(), file_path))
                return;
        }
        long file_Size = (new File(file_path)).length();
        peer.addChunk(chunk, file_Size);
        sendStored(message);
    }

    private void sendStored(StoredMessage message){
        peer.addReplicationDatabase(message);
        sleepRandomTime(400);
        peer.send(message);
    }

    private String getPeerID(){
        return peer.getPeerID();
    }

}
