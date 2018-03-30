package dbs.peer;

import com.sun.istack.internal.NotNull;
import dbs.Chunk;
import dbs.message.Message;
import dbs.message.ProcessMessage;
import dbs.network.MCB_Channel;
import dbs.network.MCR_Channel;
import dbs.network.MC_Channel;
import dbs.network.M_Channel;
import dbs.protocol.Backup;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dbs.utils.Constants.MC;
import static dbs.utils.Constants.MCB;

public class Peer implements PeerInterface, Serializable{
    private static Peer instance;
    private static Map<Pair<String, Integer>, Chunk> myChunks = new ConcurrentHashMap<>();
    private static Map<Pair<String, Integer>, Pair<Integer, HashSet<String>>> chunkReplication = new ConcurrentHashMap<>();

    private String version;
    private String peerID;
    private String accessPoint;

    private String mc_ip, mdb_ip, mdr_ip;
    private int mc_port, mdb_port, mdr_port;

    private int usageSpace = 0;
    private int availableSpace;

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

    public boolean haveChunk(Chunk chunk){
        return myChunks.containsKey(new Pair<>(getFileIDFromChunk(chunk), getChunkIDFromChunk(chunk)));
    }

    public void addChunk(Chunk chunk, long file_Size){
        if(myChunks.put(new Pair<>(getFileIDFromChunk(chunk), getChunkIDFromChunk(chunk)), chunk) == null)
            usageSpace += file_Size;
    }

    private void initPeer(){
        System.out.println("Starting peer " + peerID + "\n");
        channels[0] = new MC_Channel(mc_ip, mc_port, this);
        channels[1] = new MCB_Channel(mdb_ip, mdb_port, this);
        channels[2] = new MCR_Channel(mdr_ip, mdr_port, this);
        for (M_Channel channel: channels) {
            new Thread(channel).start();
        }
    }

    public void start() {
        initPeer();
    }

    public void send(Message message){
        switch (message.getMessageType()){
            case PUTCHUNK:
                ProcessMessage.sendMessage(message, channels[MCB]);
                break;
            case STORED:
                ProcessMessage.sendMessage(message, channels[MC]);
                break;
            default:
                break;

        }
    }

    @Override
    public void backup(String file, int replicationDegree){
        Backup backup = new Backup(file, replicationDegree, this);
        backup.run();
    }

    @Override
    public void restore(String file){

    }

    @Override
    public void delete(String file){

    }

    @Override
    public void reclaimSpace(int value){

    }

    @Override
    public void state(){

    }

    public void initReplicationDatabase(Message message){
        Pair<String, Integer> chunkIdentifier = new Pair<>(message.getFileID(), message.getChunkNO());
        Pair<Integer, HashSet<String>> peersStored = chunkReplication.get(chunkIdentifier);
        if(peersStored == null)
            peersStored = new Pair<>(message.getReplicationDeg(), new HashSet<>());
        else
            peersStored = new Pair<>(message.getReplicationDeg(), peersStored.getValue());
        chunkReplication.put(chunkIdentifier, peersStored);
    }

    public void updateReplicationDatabase(Message message){
        Pair<String, Integer> chunkIdentifier = new Pair<>(message.getFileID(), message.getChunkNO());
        Pair<Integer, HashSet<String>> peersStored = chunkReplication.get(chunkIdentifier);
        if(peersStored == null)
            peersStored = new Pair<>(null, new HashSet<>());
        peersStored.getValue().add(message.getSenderID());
    }

    public int getUsageSpace() {
        return usageSpace;
    }

    public int getAvailableSpace() {
        return availableSpace;
    }

    public int getActualRepDegree(String fileID, int chunkNO) {
        Pair<Integer, HashSet<String>> peersStored = chunkReplication.get(new Pair<>(fileID, chunkNO));
        int degree = 0;
        if(peersStored != null)
            degree = peersStored.getValue().size();
        return degree;
    }

    private String getFileIDFromChunk(@NotNull Chunk chunk){
        return chunk.getFileID();
    }

    private int getChunkIDFromChunk(@NotNull Chunk chunk){
        return chunk.getChunkID();
    }
}
