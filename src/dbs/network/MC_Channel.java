package dbs.network;

import dbs.message.Message;
import dbs.message.ProcessMessage;
import dbs.peer.Peer;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MC_Channel extends M_Channel {

    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @param peer    peer
     */
    public MC_Channel(String address, int port, Peer peer){
        super(address, port, peer);
    }

    @Override
    protected void handleRequest(DatagramPacket packet) {
        byte[] data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        String string_message = new String(data, StandardCharsets.ISO_8859_1);
        Message message = Message.parse(string_message);
        //peersConnected.put(message.getSenderID(), new Pair<>(packet.getAddress(), packet.getPort()));

        System.out.println(message.getMessageType().toString() + " " + message.getSenderID());
        ProcessMessage.processMessage(message, peer);
    }
}
