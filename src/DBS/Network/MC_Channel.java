package DBS.Network;

import java.io.IOException;

public class MC_Channel extends M_Channel {

    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @throws IOException
     */
    MC_Channel(String address, int port) throws IOException {
        super(address, port);
    }

    @Override
    public void run() {

    }
}
