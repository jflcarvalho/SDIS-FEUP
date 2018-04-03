package dbs.protocol;

import dbs.Chunk;
import dbs.message.*;
import dbs.peer.Peer;
import dbs.utils.Constants;
import javafx.util.Pair;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import static dbs.Chunk.readChunk;
import static dbs.file_io.FileManager.createFile;
import static dbs.file_io.FileManager.writeFile;
import static dbs.file_io.M_File.getFileID;
import static dbs.utils.Constants.*;
import static dbs.utils.Constants.MessageType.CHUNK;
import static dbs.utils.Utils.sleepRandomTime;
import static dbs.utils.Utils.sleepThread;

public class Restore implements Runnable {
    public final static double VERSION = 1.11;

    private String file_path;
    private File file;
    private final Peer peer;
    private double version;
    private Map<Pair<String, Integer>,Chunk> restoredChunks = new ConcurrentHashMap<>();
    private int lastChunkID;

    private static Restore instance;

    public Restore(Peer peer, double version) {
        this.peer = peer;
        this.version = version;
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
        if(chunksInfo.size() == 0){
            System.out.println("No File in System to Restore");
            return;
        }
        if(version == VERSION)
            new Thread(this::startTCPServer).start();
        for (Map.Entry<Integer, Pair<Integer, HashSet<String>>> entry : chunksInfo.entrySet())
        {
            if(entry.getValue().getValue().size() == 0){
                System.out.println("The File is not Possible to Restore");
                return;
            }
            if(!myChunks.contains(entry.getKey()))
                new Thread(() -> requestChunk(fileID, entry.getKey())).start();
            else{
                int chunkID = entry.getKey();
                new Thread(() -> addChunk(readChunk(peer.getPeerID(), fileID, chunkID))).start();
            }
        }
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
        if(tries < NUMBER_OF_TRIES)
            System.out.print(" Restored with Success\n");
        else
            System.out.print(" Unsuccessful\n");
    }

    private void startTCPServer() {
        try {
            System.out.println("Start server at: " + peer.getMdr_port());
            ServerSocket server = new ServerSocket(peer.getMdr_port());
            while (true){
                Socket connect = server.accept();
                System.out.println("Connected to : " + connect.getInetAddress().toString() + ":" + connect.getPort());
                InputStream in = connect.getInputStream();
                DataInputStream dis = new DataInputStream(in);

                int len = dis.readInt();
                byte[] data = new byte[len];
                if (len > 0) {
                    dis.readFully(data);
                }
                String string_message = new String(data, StandardCharsets.ISO_8859_1);
                ChunkMessage message = (ChunkMessage) Message.parse(string_message);
                if(message == null)
                    return;
                Constants.MessageType messageType = message.getMessageType();
                System.out.println(messageType.toString() + " " + message.getSenderID());
                if(messageType == CHUNK){
                    ProcessMessage.processChunkMessage(message, peer);
                }
            }
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Start TCP Server");
        }
    }

    public void replyChunk(String fileID, int chunkID, String senderID){
        Chunk chunk = readChunk(peer.getPeerID(), fileID, chunkID);
        if(chunk == null)
            return;
        ChunkMessage message = MessageFactory.getChunkMessage(peer.getPeerID(), chunk);
        sleepRandomTime(400);
        if(version != VERSION)
            peer.send(message);
        else
            peer.send(message, senderID);
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
        Map<Pair<String, Integer>,Chunk> sortedChunks = new TreeMap<>((o1, o2) -> {
            int fileIDComparator = o1.getKey().compareTo(o2.getKey());
            if(fileIDComparator == 0)
                return o1.getValue().compareTo(o2.getValue());
            else
                return fileIDComparator;
        });
        sortedChunks.putAll(restoredChunks);
        byte[] data = chunkAggregator(sortedChunks);
        String file_Path = "restore/" + peer.getPeerID() + "/" + file.getName();
        createFile(file_Path);
        writeFile(data, file_Path);
    }

    /**
     *
     * @param sortedChunks all received chunks to aggregate
     * @return byte[] with all chunks aggregated
     */
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

    /**
     * Singleton Design Patter
     * @return singleton instance
     */
    public static Restore getInstance(){
        return instance;
    }
}
