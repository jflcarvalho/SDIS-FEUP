package network;

import java.net.DatagramPacket;

public class DB_Channel extends M_Channel {
    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     */
    DB_Channel(String address, int port) {
        super(address, port);
    }

    @Override
    protected void handleRequest(DatagramPacket packet) {

    }
}
