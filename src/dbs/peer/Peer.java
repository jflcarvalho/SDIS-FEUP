package dbs.peer;

import dbs.Chunk;
import dbs.message.*;
import dbs.network.MCB_Channel;
import dbs.network.MCR_Channel;
import dbs.network.MC_Channel;
import dbs.network.M_Channel;
import dbs.protocol.Backup;
import dbs.protocol.Delete;
import dbs.protocol.ReclaimSpace;
import dbs.protocol.Restore;
import dbs.utils.Constants;
import javafx.util.Pair;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static dbs.file_io.FileManager.createFile;
import static dbs.file_io.M_File.getFileID;
import static dbs.utils.Constants.*;

public class Peer implements PeerInterface, Serializable {
    private Map<String, HashSet<Integer>> myChunks = new ConcurrentHashMap<>();
    private Map<String, Map<Integer, Pair<Integer, HashSet<String>>>> chunkReplication = new ConcurrentHashMap<>();
    private Map<String, ConcurrentLinkedQueue<Message>> pendingMessages = new ConcurrentHashMap<>();
    private HashSet<String> filesBackedUp = new HashSet<>();

    private String peerID;
    private final String accessPoint;

    private final String mc_ip;
    private final String mdb_ip;
    private final String mdr_ip;
    private final int mc_port;
    private final int mdb_port;
    private final int mdr_port;

    private int usageSpace = 0;

    private final M_Channel[] channels = new M_Channel[3];

    public Peer(String[] args){
        Constants.VERSION = Double.parseDouble(args[0]);
        if(!args[1].equals("-1"))
            this.peerID = args[1];
        this.accessPoint = args[2];
        this.mc_ip = args[3];
        this.mc_port = Integer.parseInt(args[4]);
        this.mdb_ip = args[5];
        this.mdb_port = Integer.parseInt(args[6]);
        this.mdr_ip = args[7];
        this.mdr_port = Integer.parseInt(args[8]);
    }

    public String getAccessPoint() {
        return accessPoint;
    }

    public String getPeerID() {
        return peerID;
    }

    public int getAvailableSpace(){
        return AVAILABLE_SPACE - usageSpace;
    }

    public Map<String, HashSet<Integer>> getMyChunks() {
        return myChunks;
    }

    public boolean haveChunk(String fileID, int chunkID) {
        HashSet<Integer> chunks = myChunks.get(fileID);
        return chunks != null && chunks.contains(chunkID);
    }

    public void addChunk(Chunk chunk, long file_Size){
        HashSet<Integer> chunks = myChunks.get(getFileIDFromChunk(chunk));
        if(chunks == null)
            chunks = new HashSet<>();
        chunks.add(chunk.getChunkID());
        myChunks.put(getFileIDFromChunk(chunk), chunks);
        usageSpace += file_Size;
    }

    public void removeChunk(String fileID, int chunkID, long file_Size){
        myChunks.get(fileID).remove(chunkID);
        usageSpace -= file_Size;
    }

    public HashSet<Integer> getChunksOfFile(String fileID) {
        return myChunks.get(fileID);
    }

    public void start() {
        initPeer();
        send(MessageFactory.getAliveMessage(peerID));
    }

    private void initPeer(){
        System.out.println("Starting peer " + peerID + "\n");
        loadData();
        channels[0] = new MC_Channel(mc_ip, mc_port, this);
        channels[1] = new MCB_Channel(mdb_ip, mdb_port, this);
        channels[2] = new MCR_Channel(mdr_ip, mdr_port, this);
        for (M_Channel channel: channels) {
            new Thread(channel).start();
        }
    }

    @Override
    public void backup(String file_path, int replicationDegree) {
        Backup backup = new Backup(file_path, replicationDegree, this);
        backup.run();
        filesBackedUp.add(file_path);
    }

    @Override
    public void restore(String file_path){
        Restore restore = new Restore(file_path, this);
        restore.run();
    }

    @Override
    public void delete(String file_path){
        Delete delete = new Delete(file_path, this);
        delete.run();
    }

    @Override
    public void reclaimSpace(int value){
        ReclaimSpace reclaimSpace = new ReclaimSpace(value, this);
        reclaimSpace.run();
    }

    @Override
    public String state(){
        StringBuilder returnString = new StringBuilder();
        returnString.append("-------- MY FILES -------\n");
        for(String file_path : filesBackedUp){
            returnString.append("--------- FILE ---------\n");
            returnString.append("File Path: ").append(file_path).append("\n");
            String fileID = getFileID(file_path);
            returnString.append("FileID: ").append(fileID).append("\n");
            returnString.append("Desired Replication Degree: ").append(getReplicationChunkMap(fileID).get(0).getKey()).append("\n");
            returnString.append("-------- CHUNKS --------\n");
            for(Map.Entry<Integer, Pair<Integer, HashSet<String>>> entry : getReplicationChunkMap(fileID).entrySet()){
                returnString.append("    ID: ").append(entry.getKey()).append(" - Actual Replication Degree: ").append(entry.getValue().getValue().size()).append("\n");
            }
        }
        returnString.append("\n------- MY CHUNKS -------\n");
        for(Map.Entry<String, HashSet<Integer>> entry : myChunks.entrySet()){
            for(Integer chunkID : entry.getValue()){
                returnString.append("CHUNK -> fileID: ").append(entry.getKey()).append(" chunkID: ").append(chunkID).append("\n");
                returnString.append("         Size: ").append(new File("backup/" + peerID + "/" + entry.getKey() + "/" + chunkID).length());
                returnString.append(" Actual Replication Degree: ").append(getActualRepDegree(entry.getKey(), chunkID)).append("\n");
            }
        }
        returnString.append("\n------- DISK INFO -------\n");
        returnString.append("Maximum Amount of Disk: " + AVAILABLE_SPACE + "\n");
        returnString.append("Available Space in Disk: ").append(getAvailableSpace()).append("\n");
        returnString.append("Used Amount of Disk: ").append(usageSpace).append("\n");
        return returnString.toString();
    }

    public void send(Message message){
        switch (message.getMessageType()){
            case PUTCHUNK:
                channels[MCB].send(message.encode());
                break;
            case CHUNK:
                channels[MCR].send(message.encode());
                break;
            default:
                channels[MC].send(message.encode());
                break;
        }
    }

    public void send(Message message, String senderID){
        switch (message.getMessageType()){
            case CHUNK:
                ((MCR_Channel) channels[MCR]).send(message.encode(), senderID);
                break;
            default:
                break;
        }
    }

    //--------- REPLICATION DEGREE ----------
    public Map<Integer, Pair<Integer, HashSet<String>>> getReplicationChunkMap(String fileID){
        Map<Integer, Pair<Integer, HashSet<String>>> chunkMap = chunkReplication.get(fileID);
        if(chunkMap == null) {
            chunkMap = new ConcurrentHashMap<>();
            chunkReplication.put(fileID, chunkMap);
        }
        return chunkMap;
    }

    private Pair<Integer, HashSet<String>> getReplicationPair(String fileID, Integer chunkID){
        Pair<Integer, HashSet<String>> chunkPair = getReplicationChunkMap(fileID).computeIfAbsent(chunkID, k -> new Pair<>(null, new HashSet<>()));
        return chunkPair;
    }

    public void updateReplicationDatabase(PutChunkMessage message){
        String fileID = getFileIDFromMessage(message);
        int chunkID = message.getChunkNO();
        Pair<Integer, HashSet<String>> chunkPair = getReplicationChunkMap(fileID).get(chunkID);
        if(chunkPair == null) {
            chunkPair = new Pair<>(message.getReplicationDeg(), new HashSet<>());
        } else {
            chunkPair = new Pair<>(message.getReplicationDeg(), chunkPair.getValue());
        }
        getReplicationChunkMap(fileID).put(chunkID, chunkPair);
    }

    public void addReplicationDatabase(StoredMessage message){
        HashSet<String> chunkInPeers = getReplicationPair(getFileIDFromMessage(message), message.getChunkNO()).getValue();
        chunkInPeers.add(message.getSenderID());
    }

    public void removeReplicationDatabase(String fileID, Integer chunkID){
        if(chunkID == null)
            chunkReplication.remove(fileID);
        else
            chunkReplication.get(fileID).get(chunkID).getValue().clear();
    }

    public void removeReplicationDatabase(RemovedMessage message){
        String fileID = message.getFileID();
        int chunkID = message.getChunkID();
        chunkReplication.get(fileID).get(chunkID).getValue().remove(message.getSenderID());
    }

    public int getActualRepDegree(String fileID, int chunkNO) {
        Pair<Integer, HashSet<String>> peersStored = getReplicationPair(fileID, chunkNO);
        return peersStored.getValue().size();
    }
    //----------

    //---------- METADATA ------------
    public void saveData(){
        String METADATA_PATH = "metadata/" + peerID + "/myChunks.ser";
        FileOutputStream fout;
        createFile(METADATA_PATH);
        try {
            fout = new FileOutputStream(METADATA_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(myChunks);
            oos.writeObject(chunkReplication);
            oos.close();
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Saving Metadata");
        }
    }

    private void loadData(){
        String METADATA_PATH = "metadata/" + peerID + "/myChunks.ser";
        try {
            FileInputStream fin = new FileInputStream(METADATA_PATH);
            ObjectInputStream ois = new ObjectInputStream(fin);
            myChunks = (Map<String, HashSet<Integer>>) ois.readObject();
            chunkReplication = (Map<String, Map<Integer, Pair<Integer, HashSet<String>>>>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Loading Metadata");
        }
    }
    //-----------

    private String getFileIDFromChunk(Chunk chunk){
        return chunk.getFileID();
    }

    private int getChunkIDFromChunk(Chunk chunk){
        return chunk.getChunkID();
    }

    private String getFileIDFromMessage(Message message){
        return message.getFileID();
    }

    public void addPendingMessage(String peerID, DeleteMessage message) {
        ConcurrentLinkedQueue<Message> messages = pendingMessages.get(peerID);
        if(messages == null){
            messages = new ConcurrentLinkedQueue<>();
        }
        messages.add(message);
        pendingMessages.put(peerID, messages);
    }

    public void sendPending(String senderID) {
        ConcurrentLinkedQueue<Message> messages = pendingMessages.get(senderID);
        if(messages != null){
            while (!messages.isEmpty())
                send(messages.poll());
        }
    }

    public int getMdr_port() {
        return channels[MCR].getPort();
    }
}
