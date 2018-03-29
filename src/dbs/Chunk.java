package dbs;

import javafx.util.Pair;

import java.util.Hashtable;

public class Chunk {
    static Hashtable<Pair<String, Integer>, Chunk> chunkTable = new Hashtable<>();

    private static int nextID = 0;
    private int chunkID;
    private String fileID;
    private byte[] data;

    public Chunk(String fileID, byte[] data) {
        this.chunkID = nextID;
        this.fileID = fileID;
        this.data = data;
        nextID++;
        chunkTable.put(new Pair<>(fileID, chunkID), this);
    }

    public Chunk(String fileID, int chunkID, byte[] data) {
        this.chunkID = chunkID;
        this.fileID = fileID;
        this.data = data;
        chunkTable.put(new Pair<>(fileID, chunkID), this);
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
}
