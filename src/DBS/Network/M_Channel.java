package DBS.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static DBS.Utils.Constants.PACKETLENGHT;

public abstract class M_Channel implements Runnable {
    private MulticastSocket mc_socket;
    private InetAddress address;
    private int port;

    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @throws IOException
     */
    M_Channel(String address, int port) throws IOException {
        this.address = InetAddress.getByName(address);
        this.port = port;

        System.out.println(address);
        System.out.println(port);

        mc_socket = new MulticastSocket(port);
        mc_socket.joinGroup(this.address);
    }

    /**
     * Receive incoming request
     *
     * @return DatagramPacket with request
     */
    public DatagramPacket receiveRequest(){
        byte[] buffer = new byte[PACKETLENGHT];

        DatagramPacket packet = new DatagramPacket(buffer, PACKETLENGHT);

        try {
            mc_socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
