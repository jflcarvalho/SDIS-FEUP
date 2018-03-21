package com.DBS.Protocol;

public class Chunk {
    static int nextID = 0;
    private int chunkID;
    private byte[] data;

    public Chunk(byte[] data) {
        this.chunkID = nextID;
        this.data = data;
        nextID++;
    }

    public int getChunkID() {
        return chunkID;
    }

    public byte[] getData() {
        return data;
    }
}
