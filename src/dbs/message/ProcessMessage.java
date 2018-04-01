package dbs.message;

import dbs.Chunk;
import dbs.peer.Peer;
import dbs.protocol.Backup;
import dbs.protocol.Delete;
import dbs.protocol.ReclaimSpace;
import dbs.protocol.Restore;

public abstract class ProcessMessage {
    public static void processMessage(Message message, Peer peer){
        switch (message.messageType){
            case STORED:
                if(!peer.getPeerID().equals(getSenderIDFromMessage(message)))
                    peer.addReplicationDatabase((StoredMessage) message);
                break;
            case DELETE:
                new Delete(peer).deleteFileFromID(message.file_ID);
                break;
            case GETCHUNK:
                new Restore(peer).replyChunk(message.file_ID, ((GetChunkMessage) message).getChunkID());
            case REMOVED:
                peer.removeReplicationDatabase((RemovedMessage) message);
                new ReclaimSpace(peer).removedChunk(message.getFileID(), ((RemovedMessage) message).getChunkID());
                break;
            default:
                break;
        }
    }

    public static void processPutChunk(PutChunkMessage message, Peer peer){
        if(peer.getPeerID().equals(getSenderIDFromMessage(message)))
            return;
        if(peer.getAvailableSpace() < message.getBody().length)
            return;
        peer.updateReplicationDatabase(message);
        Chunk chunk = Chunk.createChunkFromMessage(message);
        new Backup(peer).storeChunk(chunk);
    }

    public static void processChunkMessage(ChunkMessage message, Peer peer){
        if(peer.getPeerID().equals(getSenderIDFromMessage(message)))
            return;
        Restore restore = Restore.getInstance();
        if(restore == null)
            return;
        Chunk chunk = Chunk.createChunkFromMessage(message);
        restore.addChunk(chunk);
    }

    private static String getSenderIDFromMessage(Message message){
        return message.getSenderID();
    }
}
