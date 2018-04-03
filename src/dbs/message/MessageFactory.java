package dbs.message;

import dbs.Chunk;
import dbs.utils.Constants;


public abstract class MessageFactory {
    public static StoredMessage getStoredMessage(String peerID, Chunk chunk){
        return new StoredMessage(Constants.VERSION, peerID, getFileIDFromChunk(chunk), getChunkIDFromChunk(chunk));
    }

    public static PutChunkMessage getPutChunkMessage(String peerID, Chunk chunk, int replication_Deg) {
        return new PutChunkMessage(Constants.VERSION, peerID, getFileIDFromChunk(chunk),
                getChunkIDFromChunk(chunk), replication_Deg, getDataFromChunk(chunk));
    }

    public static DeleteMessage getDeleteMessage(String peerID, String fileID) {
        return new DeleteMessage(Constants.VERSION, peerID, fileID);
    }

    public static GetChunkMessage getGetChunkMessage(String peerID, String fileID, int chunkID) {
        return new GetChunkMessage(Constants.VERSION, peerID, fileID, chunkID);
    }

    public static ChunkMessage getChunkMessage(String peerID, Chunk chunk){
        return new ChunkMessage(Constants.VERSION, peerID, chunk.getFileID(), chunk.getChunkID(), chunk.getData());
    }

    public static RemovedMessage getRemovedMessage(String peerID, String fileID, int chunkID){
        return new RemovedMessage(Constants.VERSION, peerID, fileID, chunkID);
    }

    public static DeletedMessage getDeletedMessage(String peerID, String fileID, int chunkID){
        return new DeletedMessage(Constants.VERSION, peerID, fileID, chunkID);
    }

    public static AliveMessage getAliveMessage(String peerID){
        return new AliveMessage(1.11, peerID, "#");
    }

    private static String getFileIDFromChunk(Chunk chunk){
        return chunk.getFileID();
    }

    private static int getChunkIDFromChunk(Chunk chunk){
        return chunk.getChunkID();
    }

    private static byte[] getDataFromChunk(Chunk chunk){
        return chunk.getData();
    }

}
