package dbs.message;

import com.sun.istack.internal.NotNull;
import dbs.Chunk;
import dbs.protocol.Backup;
import dbs.protocol.Delete;
import dbs.protocol.ReclaimSpace;
import dbs.protocol.Restore;

public abstract class MessageFactory {
    public static StoredMessage getStoredMessage(String peerID, Chunk chunk){
        return new StoredMessage(Backup.VERSION, peerID, getFileIDFromChunk(chunk), getChunkIDFromChunk(chunk));
    }

    public static PutChunkMessage getPutChunkMessage(String peerID, Chunk chunk, int replication_Deg) {
        return new PutChunkMessage(Backup.VERSION, peerID, getFileIDFromChunk(chunk),
                getChunkIDFromChunk(chunk), replication_Deg, getDataFromChunk(chunk));
    }

    public static DeleteMessage getDeleteMessage(String peerID, String fileID) {
        return new DeleteMessage(Delete.VERSION, peerID, fileID);
    }

    public static GetChunkMessage getGetChunkMessage(String peerID, String fileID, int chunkID) {
        return new GetChunkMessage(Delete.VERSION, peerID, fileID, chunkID);
    }

    public static ChunkMessage getChunkMessage(String peerID, Chunk chunk){
        return new ChunkMessage(Restore.VERSION, peerID, chunk.getFileID(), chunk.getChunkID(), chunk.getData());
    }

    public static RemovedMessage getRemovedMessage(String peerID, String fileID, int chunkID){
        return new RemovedMessage(ReclaimSpace.VERSION, peerID, fileID, chunkID);
    }

    private static String getFileIDFromChunk(@NotNull Chunk chunk){
        return chunk.getFileID();
    }

    private static int getChunkIDFromChunk(@NotNull Chunk chunk){
        return chunk.getChunkID();
    }

    private static byte[] getDataFromChunk(@NotNull Chunk chunk){
        return chunk.getData();
    }

}
