package dbs.message;

import dbs.Chunk;
import dbs.protocol.Backup;

import static dbs.protocol.Backup.VERSION;
import static dbs.utils.Constants.MessageType.PUTCHUNK;
import static dbs.utils.Constants.MessageType.STORED;

public abstract class MessageFactory {
    public static Message getStoredMessage(String peerID, Chunk chunk){
        return new Message(STORED, VERSION, peerID, chunk.getFileID(), chunk.getChunkID());
    }

    public static Message getPutChunkMessage(Backup backup, Chunk chunk) {
        return new Message(PUTCHUNK, VERSION, backup.getPeer().getPeerID(), chunk.getFileID(), chunk.getChunkID(), backup.getReplicationDegree(), chunk.getData());
    }
}
