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

    public static void processMessage(Message message){
        Chunk chunk = new Chunk(message.getFileID(), message.getChunkNO(), message.getBody());
        Peer peer = Peer.getInstance();
        switch (message.getMessageType()){
            case STORED:
                peer.updateReplicationOfFile(chunk, message.getSenderID());
                break;
            default:
                break;
        }
    }

    public static void processPutChunk(Message message){
        if(Peer.getInstance().getPeerID().equals(message.getSenderID()))
            return;
        Chunk chunk = new Chunk(message.getFileID(), message.getChunkNO(), message.getBody());
        new Backup(Peer.getInstance()).storeChunk(chunk);
    }
}
