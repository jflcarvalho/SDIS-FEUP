package dbs.protocol;

import dbs.message.DeleteMessage;
import dbs.message.DeletedMessage;
import dbs.message.MessageFactory;
import dbs.peer.Peer;
import javafx.util.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static dbs.file_io.FileManager.deleteFile;
import static dbs.file_io.M_File.getFileID;
import static dbs.utils.Constants.NUMBER_OF_TRIES;
import static dbs.utils.Constants.SLEEP_TIME;
import static dbs.utils.Utils.sleepThread;

public class Delete implements Runnable {
    public final static double VERSION = 1.11;
    private HashMap<String, HashSet<String>> pendingDeletion = new HashMap<>();

    private String file_path;
    private final Peer peer;
    private double version;

    public Delete(Peer peer, double version) {
        this.peer = peer;
        this.version = version;
    }

    public Delete(String file_path, Peer peer){
        this.file_path = file_path;
        this.peer = peer;
        version = VERSION;
    }

    public void run() {
        System.out.println("\n----------------- DELETE --------------------\n");
        System.out.println("Deleting " + file_path);
        String fileID = getFileID(file_path);
        sendDelete(fileID);
        System.out.println("\n------------------ END ---------------------\n");
    }

    private void sendDelete(String fileID){
        DeleteMessage message = MessageFactory.getDeleteMessage(peer.getPeerID(), fileID);
        int tries = 0;
        boolean degree;
        do {
            peer.send(message);
            sleepThread(SLEEP_TIME * (tries < 1 ? 1 : 2));
            degree = getLeftChunkDeletion(fileID);
            tries++;
        }while (tries < NUMBER_OF_TRIES && degree);
        createPendingMessages(message);
    }

    private void createPendingMessages(DeleteMessage message) {
        for(Map.Entry<String, HashSet<String>> entry : pendingDeletion.entrySet()){
            for (String peerID : entry.getValue()){
                peer.addPendingMessage(peerID, message);
            }
        }
    }

    public void deleteFileFromID(String fileID) {
        HashSet<Integer> chunks = peer.getChunksOfFile(fileID);
        if(chunks == null || chunks.size() == 0) {
            if(version != VERSION)
                peer.removeReplicationDatabase(fileID, null);
            return;
        }
        chunks = (HashSet<Integer>) chunks.clone();
        for (Integer chunkID : chunks) {
            new Thread(() -> deleteChunk(fileID, chunkID)).start();
        }
    }

    private void deleteChunk(String fileID, int chunkID){
        String file_path = "backup/" + peer.getPeerID() + "/" + fileID + "/" + chunkID;
        int tries = 0;
        long file_Size = (new File(file_path)).length();
        peer.removeChunk(fileID, chunkID, file_Size);
        if(version != VERSION)
            peer.removeReplicationDatabase(fileID, chunkID);
        else{
            DeletedMessage message = MessageFactory.getDeletedMessage(peer.getPeerID(), fileID, chunkID);
            peer.send(message);
        }

        while (!deleteFile(file_path) && tries < 3)
            tries++;
    }

    private boolean getLeftChunkDeletion(String fileID){
        boolean ret = false;
        Map<Integer, Pair<Integer, HashSet<String>>> chunksOfFile =  peer.getReplicationChunkMap(fileID);
        for(Map.Entry<Integer, Pair<Integer, HashSet<String>>> entry : chunksOfFile.entrySet()){
            if(entry.getValue().getValue().size() > 0) {
                HashSet<String> peersLeft = pendingDeletion.get(fileID);
                if (peersLeft == null)
                    peersLeft = new HashSet<>();
                peersLeft.addAll(entry.getValue().getValue());
                pendingDeletion.put(fileID, peersLeft);
                ret = true;
            }
        }
        return ret;
    }
}
