package dbs.peer;

import com.sun.istack.internal.NotNull;
import dbs.Chunk;
import dbs.message.Message;
import dbs.message.MessageFactory;
import dbs.message.ProcessMessage;
import dbs.network.MCB_Channel;
import dbs.network.MCR_Channel;
import dbs.network.MC_Channel;
import dbs.network.M_Channel;
import dbs.protocol.Backup;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static dbs.utils.Constants.MC;
import static dbs.utils.Constants.MCB;

public class Peer implements PeerInterface, Serializable{
    private static Peer instance;
    private static Map<Pair<String, Integer>, Chunk> myChunks = new ConcurrentHashMap<>();
    private static Map<Pair<String, Integer>, HashSet<String>> chunkReplication = new ConcurrentHashMap<>();

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
        return myChunks.containsKey(new Pair<>(chunk.getFileID(), chunk.getChunkID()));
    }

    public void addChunk(Chunk chunk, long file_Size){
        if(myChunks.put(new Pair<>(chunk.getFileID(), chunk.getChunkID()), chunk) == null)
            usageSpace += file_Size;
        send(MessageFactory.getStoredMessage(peerID, chunk));
    }

    private void initPeer(){
        System.out.println("Starting peer " + peerID + "\n");
        channels[0] = new MC_Channel(mc_ip, mc_port);
        channels[1] = new MCB_Channel(mdb_ip, mdb_port);
        channels[2] = new MCR_Channel(mdr_ip, mdr_port);
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

    public void updateReplicationOfFile(Chunk chunk, String senderID){
        Pair<String, Integer> chunkIdentifier = new Pair<>(chunk.getFileID(), chunk.getChunkID());
        HashSet<String> peersStored = chunkReplication.get(chunkIdentifier);
        if(peersStored == null)
            peersStored = new HashSet<>();
        peersStored.add(senderID);
        chunkReplication.put(chunkIdentifier, peersStored);
    }

    public int getUsageSpace() {
        return usageSpace;
    }

    public int getAvailableSpace() {
        return availableSpace;
    }

    public static Peer getInstance(){
        return instance;
    }

    public int getDegree(String fileID, int chunkNO) {
        HashSet<String> degrees = chunkReplication.get(new Pair<>(fileID, chunkNO));
        int degree = 0;
        if(degrees != null)
            degree = degrees.size();
        return degree;
    }
}
