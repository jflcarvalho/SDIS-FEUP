package dbs.message;

import dbs.Chunk;
import dbs.network.M_Channel;
import dbs.peer.Peer;
import dbs.protocol.Backup;
import dbs.protocol.Delete;

import static dbs.utils.Constants.MessageType.PUTCHUNK;

public abstract class ProcessMessage {
    public static void sendMessage(Message message, M_Channel channel){
        byte[] header = message.encode().getBytes();
        if(message.getMessageType() != PUTCHUNK){
            channel.send(header);
            return;
        }

        //In case of message be a PUTCHUNK to append body to header
        byte[] body = ((PutChunkMessage) message).getBody();
        byte[] packet = new byte[header.length + body.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(body, 0, packet, header.length, body.length);
        channel.send(packet);
    }

    public static void processMessage(Message message, Peer peer){
        switch (message.messageType){
            case STORED:
                if(!peer.getPeerID().equals(getSenderIDFromMessage(message)))
                    peer.addReplicationDatabase((StoredMessage) message);
                break;
            case DELETE:
                new Delete(peer).deleteFileFromID(message.file_ID);
                break;
            default:
                break;
        }
    }

    public static void processPutChunk(PutChunkMessage message, Peer peer){
        if(peer.getPeerID().equals(getSenderIDFromMessage(message)))
            return;
        if(peer.getRemainSpace() < message.getBody().length)
            return;
        peer.updateReplicationDatabase(message);
        Chunk chunk = Chunk.createChunkFromMessage(message);
        new Backup(peer).storeChunk(chunk);
    }

    private static String getSenderIDFromMessage(Message message){
        return message.getSenderID();
    }
}
