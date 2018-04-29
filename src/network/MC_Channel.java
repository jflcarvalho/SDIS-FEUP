package network;

import java.net.DatagramPacket;
import java.util.Arrays;

public class MC_Channel extends M_Channel {

    /**
     * Class that connects and listens to a Control multicast channel
     *
     * @param address multicast address
     * @param port    multicast port
     */
    public MC_Channel(String address, int port){
        super(address, port);
    }

    @Override
    protected void handleRequest(DatagramPacket packet) {
        byte[] data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
    }
}
