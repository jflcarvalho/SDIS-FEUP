package dbs.protocol;

import dbs.Chunk;
import dbs.message.ChunkMessage;
import dbs.message.GetChunkMessage;
import dbs.message.MessageFactory;
import dbs.peer.Peer;
import javafx.util.Pair;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import static dbs.file_io.FileManager.*;
import static dbs.file_io.M_File.getFileID;
import static dbs.utils.Constants.*;
import static dbs.utils.Utils.sleepRandomTime;
import static dbs.utils.Utils.sleepThread;

public class Restore implements Runnable {
    public final static double VERSION = 1.0;

    private String file_path;
    private File file;
    private Peer peer;
    private Map<Pair<String, Integer>,Chunk> restoredChunks = new ConcurrentHashMap<>();
    private int lastChunkID;

    private static Restore instance;

    public Restore(Peer peer) {
        this.peer = peer;
    }

    public Restore(String file_path, Peer peer) {
        this.file_path = file_path;
        this.peer = peer;
    }

    public void run() {
        instance = this;
        System.out.println("\n----------------- RESTORE --------------------\n");
        System.out.println("Restoring " + file_path);
        file = new File(file_path);
        String fileID = getFileID(file_path);
        restoreChunks(fileID);
        System.out.println("\n------------------- END ---------------------\n");
    }

    private void restoreChunks(String fileID){
        Map<Integer, Pair<Integer, HashSet<String>>> chunksInfo = peer.getReplicationChunkMap(fileID);
        if(chunksInfo == null)
            return;
        HashSet<Integer> myChunks = peer.getChunksOfFile(fileID);
        if(myChunks == null)
            myChunks = new HashSet<>();
        for (Map.Entry<Integer, Pair<Integer, HashSet<String>>> entry : chunksInfo.entrySet())
        {
            if(!myChunks.contains(entry.getKey()))
                new Thread(() -> requestChunk(fileID, entry.getKey())).start();
            else{
                int chunkID = entry.getKey();
                new Thread(() -> addChunk(readChunk(fileID, chunkID))).start();
            }
        }
        //TODO: check if i already have all chunk to create the original file
    }

    private void requestChunk(String fileID, int chunkID) {
        GetChunkMessage message = MessageFactory.getGetChunkMessage(peer.getPeerID(), fileID, chunkID);
        int tries = 0;
        do {
            peer.send(message);
            sleepThread(SLEEP_TIME);
            tries++;
        }while (tries < NUMBER_OF_TRIES && !restoredChunks.containsKey(new Pair<>(fileID, chunkID)));

        System.out.print("Chunk nº: " + chunkID);
        if(tries <= NUMBER_OF_TRIES)
            System.out.print(" Restored with Success\n");
        else
            System.out.print(" Unsuccessful\n");
    }

    public void replyChunk(String fileID, int chunkID){
        Chunk chunk = readChunk(fileID, chunkID);
        if(chunk == null)
            return;
        ChunkMessage message = MessageFactory.getChunkMessage(peer.getPeerID(), chunk);
        sleepRandomTime(400);
        peer.send(message);
    }

    private Chunk readChunk(String fileID, int chunkID){
        String file_path = "backup/" + peer.getPeerID() + "/" + fileID + "/" + chunkID;
        byte[] chunkData = readFile(file_path);
        if(chunkData == null)
            return null;
        return new Chunk(fileID, chunkID, chunkData);
    }

    public void addChunk(Chunk chunk) {
        if(restoredChunks.put(new Pair<>(chunk.getFileID(), chunk.getChunkID()), chunk) != null)
            return;
        if(chunk.getData().length < CHUNK_SIZE)
            lastChunkID = chunk.getChunkID();
        if(restoredChunks.size() == lastChunkID + 1)
            recreatingFile();
    }

    private void recreatingFile() {
        Map<Pair<String, Integer>,Chunk> sortedChunks = new TreeMap<>(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                int fileIDComparator = o1.getKey().compareTo(o2.getKey());
                if(fileIDComparator == 0)
                    return o1.getValue().compareTo(o2.getValue());
                else
                    return fileIDComparator;
            }
        });
        sortedChunks.putAll(restoredChunks);
        byte[] data = chunkAggregator(sortedChunks);
        String file_Path = "restore/" + peer.getPeerID() + "/" + file.getName();
        createFile(file_Path);
        writeFile(data, file_Path);
    }

    private byte[] chunkAggregator(Map<Pair<String, Integer>, Chunk> sortedChunks) {
        byte[] allBytes = new byte[0];
        for(Map.Entry<Pair<String, Integer>,Chunk> entry: sortedChunks.entrySet()){
            byte[] chunkArray = entry.getValue().getData();
            byte[] packet = new byte[allBytes.length + chunkArray.length];
            System.arraycopy(allBytes, 0, packet, 0, allBytes.length);
            System.arraycopy(chunkArray, 0, packet, allBytes.length, chunkArray.length);
            allBytes = packet;
        }
        return allBytes;
    }

    public static Restore getInstance(){
        return instance;
    }
}
