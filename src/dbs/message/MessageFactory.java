package dbs.message;

import com.sun.istack.internal.NotNull;
import dbs.Chunk;
import dbs.protocol.Backup;
import dbs.protocol.Delete;

public abstract class MessageFactory {
    public static StoredMessage getStoredMessage(String peerID, Chunk chunk){
        return new StoredMessage(Backup.VERSION, peerID, getFileIDFromChunk(chunk), getChunkIDFromChunk(chunk));
    }

    public static PutChunkMessage getPutChunkMessage(Backup backup, Chunk chunk) {
        PutChunkMessage message = new PutChunkMessage(Backup.VERSION, backup.getPeer().getPeerID(), getFileIDFromChunk(chunk));
        message.addChunkInfo(getChunkIDFromChunk(chunk), backup.getReplicationDegree(), getDataFromChunk(chunk));
        return message;
    }

    public static DeleteMessage getDeleteMessage(String peerID, String fileID) {
        return new DeleteMessage(Delete.VERSION, peerID, fileID);
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
