package dbs.network;

import dbs.message.Message;
import dbs.message.ProcessMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MC_Channel extends M_Channel {

    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @throws IOException
     */
    public MC_Channel(String address, int port){
        super(address, port);
    }

    @Override
    protected void handleRequest(DatagramPacket packet) {
        byte[] data = packet.getData();
        String string_message = new String(Arrays.copyOfRange(data, 0, packet.getLength()), StandardCharsets.US_ASCII);
        Message message = Message.parse(string_message);
        System.out.println(message.getMessageType().toString());
        ProcessMessage.processMessage(message);
    }
}
