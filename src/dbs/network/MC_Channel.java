package dbs.network;

import java.io.IOException;

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
    void handleRequest(String string_message) {

    }
}
