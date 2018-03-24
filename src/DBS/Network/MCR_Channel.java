package DBS.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class MCR_Channel extends M_Channel {
    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @throws IOException
     */
    MCR_Channel(String address, int port) throws IOException {
        super(address, port);
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = this.receiveRequest();
            String message = new String(packet.getData(), StandardCharsets.UTF_8);

        }
    }
}
