package com.DBS.Peer;

import com.DBS.Chunk;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.util.Hashtable;

import static com.DBS.Utils.Utils.*;

public class Peer implements PeerInterface{
    static Hashtable<Pair<String, Integer>, Chunk> myChunks = new Hashtable<>();

    private String peerID;
    private int usageSpace = 0;
    private int availableSpace;

    public Peer() {
        peerID = hashString(getHex(getMAC()));
    }

    public String getPeerID() {
        return peerID;
    }

    public void addChunk(Chunk chunk, long file_Size){
        myChunks.put(new Pair<>(chunk.getFileID(), chunk.getChunkID()), chunk);
        usageSpace += file_Size;
    }

    @Override
    public void backup(String file, int replicationDegree) throws RemoteException {

    }

    @Override
    public void restore(String file) throws RemoteException {

    }

    @Override
    public void delete(String file) throws RemoteException {

    }

    @Override
    public void reclaimSpace(int value) throws RemoteException {

    }

    @Override
    public void state() throws RemoteException {

    }
}
