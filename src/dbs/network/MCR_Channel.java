package dbs.network;

import dbs.message.ChunkMessage;
import dbs.message.Message;
import dbs.message.ProcessMessage;
import dbs.peer.Peer;
import dbs.utils.Constants;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static dbs.utils.Constants.MessageType.CHUNK;

public class MCR_Channel extends M_Channel {
    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @param peer    peer
     */
    public MCR_Channel(String address, int port, Peer peer){
        super(address, port, peer);
    }

    @Override
    protected void handleRequest(DatagramPacket packet) {
        byte[] data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        String string_message = new String(data, StandardCharsets.ISO_8859_1);
        ChunkMessage message = (ChunkMessage) Message.parse(string_message);
        if(message == null)
            return;
        
        Constants.MessageType messageType = message.getMessageType();
        System.out.println(messageType.toString() + " " + message.getSenderID());
        if(messageType == CHUNK){
            ProcessMessage.processChunkMessage(message, peer);
        }
    }
}
