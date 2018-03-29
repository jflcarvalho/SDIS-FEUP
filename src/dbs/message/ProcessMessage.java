package dbs.message;

import dbs.Chunk;
import dbs.network.M_Channel;
import dbs.peer.Peer;
import dbs.protocol.Backup;

public abstract class ProcessMessage {
    public static void sendMessage(Message message, M_Channel channel){
        byte[] header = Message.encode(message).getBytes();
        byte[] body = message.getBody();
        if(body == null) {
            channel.send(header);
           return;
        }
        byte[] packet = new byte[header.length + body.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(body, 0, packet, header.length, body.length);
        channel.send(packet);
    }

    public static void processMessage(Message message, Peer peer){
        Chunk chunk = Chunk.createChunkFromMessage(message);
        switch (message.getMessageType()){
            case STORED:
                peer.updateReplicationOfFile(chunk, getSenderIDFromMessage(message));
                break;
            default:
                break;
        }
    }

    public static void processPutChunk(Message message, Peer peer){
        if(peer.getPeerID().equals(getSenderIDFromMessage(message)))
            return;
        Chunk chunk = Chunk.createChunkFromMessage(message);
        new Backup(peer).storeChunk(chunk);
    }

    private static String getSenderIDFromMessage(Message message){
        return message.getSenderID();
    }
}
