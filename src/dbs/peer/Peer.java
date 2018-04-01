package dbs.peer;

import com.sun.istack.internal.NotNull;
import dbs.Chunk;
import dbs.message.*;
import dbs.network.MCB_Channel;
import dbs.network.MCR_Channel;
import dbs.network.MC_Channel;
import dbs.network.M_Channel;
import dbs.protocol.Backup;
import dbs.protocol.Delete;
import dbs.protocol.Restore;
import javafx.util.Pair;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dbs.file_io.FileManager.createFile;
import static dbs.utils.Constants.*;

public class Peer implements PeerInterface, Serializable{
    private static Peer instance;
    private Map<String, HashSet<Integer>> myChunks = new ConcurrentHashMap<>();
    private Map<String, Map<Integer, Pair<Integer, HashSet<String>>>> chunkReplication = new ConcurrentHashMap<>();

    private String version;
    private String peerID;
    private String accessPoint;

    private String mc_ip, mdb_ip, mdr_ip;
    private int mc_port, mdb_port, mdr_port;

    private int usageSpace = 0;
    private int availableSpace = AVAILABLE_SPACE;

    private M_Channel[] channels = new M_Channel[3];

    public Peer(@NotNull String[] args){
        this.version = args[0];
        if(!args[1].equals("-1"))
            this.peerID = args[1];
        this.accessPoint = args[2];
        this.mc_ip = args[3];
        this.mc_port = Integer.parseInt(args[4]);
        this.mdb_ip = args[5];
        this.mdb_port = Integer.parseInt(args[6]);
        this.mdr_ip = args[7];
        this.mdr_port = Integer.parseInt(args[8]);
        instance = this;
    }

    public String getPeerID() {
        return peerID;
    }

    public int getRemainSpace(){
        return availableSpace - usageSpace;
    }

    public boolean haveChunk(Chunk chunk){
        HashSet<Integer> chunks = myChunks.get(getFileIDFromChunk(chunk));
        if(chunks == null)
            return false;
        return chunks.contains(chunk.getChunkID());
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
        removeReplicationDatabase(fileID, chunkID);
        usageSpace -= file_Size;
    }

    public HashSet<Integer> getChunksOfFile(String fileID) {
        return myChunks.get(fileID);
    }

    public void start() {
        initPeer();
        //TODO: add pending messages
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
    public void backup(String file_path, int replicationDegree){
        Backup backup = new Backup(file_path, replicationDegree, this);
        backup.run();
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

    }

    @Override
    public void state(){

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
        Pair<Integer, HashSet<String>> chunkPair = getReplicationChunkMap(fileID).get(chunkID);
        if(chunkPair == null) {
            chunkPair = new Pair<>(null, new HashSet<>());
            getReplicationChunkMap(fileID).put(chunkID, chunkPair);
        }
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

    public void removeReplicationDatabase(String fileID, int chunkID){
        chunkReplication.get(fileID).get(chunkID).getValue().clear();
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

    private String getFileIDFromChunk(@NotNull Chunk chunk){
        return chunk.getFileID();
    }

    private int getChunkIDFromChunk(@NotNull Chunk chunk){
        return chunk.getChunkID();
    }

    private String getFileIDFromMessage(@NotNull Message message){
        return message.getFileID();
    }
}
