package dbs.protocol;

import dbs.Chunk;
import dbs.message.MessageFactory;
import dbs.message.RemovedMessage;
import dbs.peer.Peer;
import javafx.util.Pair;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static dbs.file_io.FileManager.deleteFile;
import static dbs.utils.Utils.sleepRandomTime;

public class ReclaimSpace implements Runnable {
    public final static double VERSION = 1.0;

    private static Map<Pair<String, Integer>, Boolean> sendBackup = new ConcurrentHashMap<>();
    private static Map<Pair<String, Integer>, Boolean> reclaimedChunks = new ConcurrentHashMap<>();

    private int necessary_Space;
    private Peer peer;


    public ReclaimSpace(Peer peer) {
        this.peer = peer;
    }

    public ReclaimSpace(int necessary_Space, Peer peer) {
        this.necessary_Space = necessary_Space;
        this.peer = peer;
    }

    public void run() {
        System.out.println("\n------------- RECLAIM SPACE ----------------\n");
        System.out.println("Reclaiming " + necessary_Space + " Kb of Space");
        manageSpace(true);
        System.out.println("\n------------------ END ---------------------\n");
    }

    private void sendRemoved(String fileID, int chunkID){
        RemovedMessage message = MessageFactory.getRemovedMessage(peer.getPeerID(), fileID, chunkID);
        peer.send(message);
    }

    public void manageSpace(boolean canDelete) {
        if(peer.getAvailableSpace() > necessary_Space)
            return;
        PriorityQueue<Pair<String, Integer>> chunksOrdered = getChunksOrdered(peer.getMyChunks());
        while(peer.getAvailableSpace() < necessary_Space){
            Pair<String, Integer> chunkToDelete = chunksOrdered.poll();
            if(chunkToDelete == null){
                System.out.println("Can't Reclaim More Space");
                return;
            }
            if(getReplicationDiff(chunkToDelete.getKey(), chunkToDelete.getValue()) < 0 || canDelete){
                reclaimedChunks.put(new Pair<>(chunkToDelete.getKey(), chunkToDelete.getValue()), true);
                deleteChunk(chunkToDelete.getKey(), chunkToDelete.getValue());
            }
        }
    }

    private void deleteChunk(String fileID, int chunkID){
        String file_path = "backup/" + peer.getPeerID() + "/" + fileID + "/" + chunkID;
        long file_Size = (new File(file_path)).length();
        if(deleteFile(file_path)){
            peer.removeChunk(fileID, chunkID, file_Size);
            new Thread(() -> sendRemoved(fileID, chunkID)).start();
        }
    }

    private PriorityQueue<Pair<String, Integer>> getChunksOrdered(Map<String, HashSet<Integer>> myChunks){
        PriorityQueue<Pair<String, Integer>> sortedChunks = new PriorityQueue<>(Comparator.comparingInt(o -> getReplicationDiff(o.getKey(), o.getValue())));
        for(Map.Entry<String, HashSet<Integer>> entry : myChunks.entrySet()){
            for(Integer chunkID : entry.getValue())
            sortedChunks.add(new Pair<>(entry.getKey(), chunkID));
        }
        return sortedChunks;
    }

    private int getReplicationDiff(String fileID, int chunkID){
        int pretendedReplication = peer.getReplicationChunkMap(fileID).get(chunkID).getKey();
        int actualReplication = peer.getActualRepDegree(fileID, chunkID);
        return pretendedReplication - actualReplication;
    }

    public void removedChunk(String fileID, int chunkID){
        if(!peer.haveChunk(fileID, chunkID))
            return;
        Pair<String, Integer> chunkPair = new Pair<>(fileID, chunkID);
        sendBackup.put(chunkPair, true);
        int pretendedReplication = peer.getReplicationChunkMap(fileID).get(chunkID).getKey();
        if(getReplicationDiff(fileID, chunkID) > 0){
            Chunk chunkToSend = Chunk.readChunk(peer.getPeerID(), fileID, chunkID);
            sleepRandomTime(400);
            if(sendBackup.get(chunkPair)){
                new Backup(peer, Backup.VERSION).sendChunk(chunkToSend, pretendedReplication);
                removeSendChunk(fileID, chunkID);
            }
        }
    }

    public static void removeSendChunk(String fileID, int chunkID){
        sendBackup.put(new Pair<>(fileID, chunkID), false);
    }

    public static boolean haveBeReclaimed(String fileID, int chunkID){
        return reclaimedChunks.remove(new Pair<>(fileID, chunkID)) != null;
    }
}
