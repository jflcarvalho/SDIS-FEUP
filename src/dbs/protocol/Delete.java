package dbs.protocol;

import dbs.message.DeleteMessage;
import dbs.message.MessageFactory;
import dbs.peer.Peer;

import java.io.File;
import java.util.HashSet;

import static dbs.file_io.FileManager.deleteFile;
import static dbs.file_io.M_File.getFileID;

public class Delete implements Runnable {
    public final static double VERSION = 1.0;

    private String file_path;
    private final Peer peer;

    public Delete(Peer peer) {
        this.peer = peer;
    }

    public Delete(String file_path, Peer peer){
        this.file_path = file_path;
        this.peer = peer;
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
        peer.send(message);
    }

    public void deleteFileFromID(String fileID) {
        HashSet<Integer> chunks = peer.getChunksOfFile(fileID);
        if(chunks == null || chunks.size() == 0) {
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
        peer.removeReplicationDatabase(fileID, chunkID);
        while (!deleteFile(file_path) && tries < 3)
            tries++;
    }
}
