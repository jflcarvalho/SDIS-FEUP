package DBS.Network;

import java.io.IOException;

public class MCB_Channel extends M_Channel {
    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @throws IOException
     */
    MCB_Channel(String address, int port) throws IOException {
        super(address, port);
    }

    @Override
    public void run() {

    }
}
