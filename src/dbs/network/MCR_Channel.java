package dbs.network;

import java.io.IOException;

public class MCR_Channel extends M_Channel {
    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @throws IOException
     */
    public MCR_Channel(String address, int port){
        super(address, port);
    }

    @Override
    void handleRequest(String string_message) {

    }

}
