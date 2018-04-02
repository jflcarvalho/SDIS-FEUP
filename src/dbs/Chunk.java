package dbs;

import dbs.message.ChunkMessage;

import java.io.Serializable;

import static dbs.file_io.FileManager.readFile;

public class Chunk implements Serializable{
    private static int nextID;
    private final int chunkID;
    private final String fileID;
    private final byte[] data;

    public Chunk(String fileID, byte[] data) {
        this.chunkID = nextID;
        this.fileID = fileID;
        this.data = data;
        nextID++;
    }

    private Chunk(String fileID, int chunkID, byte[] data) {
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

    public static Chunk createChunkFromMessage(ChunkMessage message){
        return new Chunk(message.getFileID(), message.getChunkNO(), message.getBody());
    }

    public static Chunk readChunk(String peerID, String fileID, int chunkID){
        String file_path = "backup/" + peerID + "/" + fileID + "/" + chunkID;
        byte[] chunkData = readFile(file_path);
        if(chunkData == null)
            return null;
        return new Chunk(fileID, chunkID, chunkData);
    }
}
