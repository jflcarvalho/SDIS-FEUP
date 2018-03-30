package dbs;

import dbs.message.PutChunkMessage;

import java.io.Serializable;

public class Chunk implements Serializable{
    private static int nextID;
    private int chunkID;
    private String fileID;
    private byte[] data;

    public Chunk(String fileID, byte[] data) {
        this.chunkID = nextID;
        this.fileID = fileID;
        this.data = data;
        nextID++;
    }

    public Chunk(String fileID, int chunkID, byte[] data) {
        this.chunkID = chunkID;
        this.fileID = fileID;
        this.data = data;
    }

    public String getFileID() {
        return fileID;
    }

    public int getChunkID() {
        return chunkID;
    }

    public byte[] getData() {
        return data;
    }

    public static void resetID(){
        nextID = 0;
    }

    public static Chunk createChunkFromMessage(PutChunkMessage message){
        return new Chunk(message.getFileID(), message.getChunkNO(), message.getBody());
    }
}
