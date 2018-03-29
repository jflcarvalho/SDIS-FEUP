package dbs.peer;

import dbs.Chunk;
import dbs.message.Message;
import dbs.network.MCB_Channel;
import dbs.network.MCR_Channel;
import dbs.network.MC_Channel;
import dbs.network.M_Channel;
import dbs.protocol.Backup;
import com.sun.istack.internal.NotNull;
import javafx.util.Pair;

import java.util.Hashtable;

import static dbs.utils.Constants.MessageType.PUTCHUNK;
import static dbs.utils.Utils.*;

public class Peer implements PeerInterface{
    static Hashtable<Pair<String, Integer>, Chunk> myChunks = new Hashtable<>();

    private String version;
    private String peerID;
    private String accessPoint;

    private String mc_ip, mdb_ip, mdr_ip;
    private int mc_port, mdb_port, mdr_port;

    private int usageSpace = 0;
    private int availableSpace;

    private M_Channel[] channels = new M_Channel[3];

    public Peer() {
        peerID = hashString(getHex(getMAC()));
    }

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
    }

    public String getPeerID() {
        return peerID;
    }

    public void addChunk(Chunk chunk, long file_Size){
        myChunks.put(new Pair<>(chunk.getFileID(), chunk.getChunkID()), chunk);
        usageSpace += file_Size;
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
        if(message.getMessageType() == PUTCHUNK){
            byte[] header = Message.encode(message).getBytes();
            byte[] body = message.getBody();
            byte[] packet = new byte[header.length + body.length];
            System.arraycopy(header,0,packet,0,header.length);
            System.arraycopy(body,0,packet,header.length,body.length);

            channels[1].send(packet);
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

    public int getUsageSpace() {
        return usageSpace;
    }

    public int getAvailableSpace() {
        return availableSpace;
    }
}
