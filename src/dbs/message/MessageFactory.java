package dbs.message;

import com.sun.istack.internal.NotNull;
import dbs.Chunk;
import dbs.protocol.Backup;

import static dbs.protocol.Backup.VERSION;
import static dbs.utils.Constants.MessageType.PUTCHUNK;
import static dbs.utils.Constants.MessageType.STORED;

public abstract class MessageFactory {
    public static Message getStoredMessage(String peerID, Chunk chunk){
        return new Message(STORED, VERSION, peerID, getFileIDFromChunk(chunk), getChunkIDFromChunk(chunk));
    }

    public static Message getPutChunkMessage(Backup backup, Chunk chunk) {
        Message message = new Message(PUTCHUNK, VERSION, backup.getPeer().getPeerID(), backup.getReplicationDegree());
        message.addChunkInfo(getFileIDFromChunk(chunk), getChunkIDFromChunk(chunk), getDataFromChunk(chunk));
        return message;
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
