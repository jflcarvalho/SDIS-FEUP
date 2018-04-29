package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

import static utils.Constants.DEBUG;
import static utils.Constants.PACKET_LENGTH;

public abstract class M_Channel implements Runnable {
    private MulticastSocket mc_socket;
    private InetAddress address;
    private int port;
    static HashMap<String, InetAddress> peersConnected = new HashMap<>();

    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     */
    M_Channel(String address, int port) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = port;
            System.out.println(this.getClass().getName() + " - " + address + ":" + port);

            mc_socket = new MulticastSocket(port);
            mc_socket.joinGroup(this.address);
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Starting Channel" + " - " + address + ":" + port);
        }
    }

    /**
     * @return InetAdress of the channel
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * @return the port to which the channel is connected
     */
    public int getPort() {
        return port;
    }

    /**
     * Method to handle any request received, just the derived channel know that to do with de packet
     *
     * @param packet Received packet from the channel
     */
    protected abstract void handleRequest(DatagramPacket packet);

    /**
     * Start receiving packets cycle
     */
    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = this.receiveRequest();
            new Thread(() -> handleRequest(packet)).start();
        }
    }

    /**
     * Receive incoming request
     *
     * @return DatagramPacket with request
     */
    private DatagramPacket receiveRequest(){
        byte[] buffer = new byte[PACKET_LENGTH];

        DatagramPacket packet = new DatagramPacket(buffer, PACKET_LENGTH);

        try {
            mc_socket.receive(packet);
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Receiving DatagramPacket");
        }

        return packet;
    }

    /**
     * Send packet byte[] to Multicast Channel
     *
     * @param packetBody byte[] of info to send
     */
    public void send(byte[] packetBody) {
        try {
            mc_socket.send(new DatagramPacket(packetBody, packetBody.length, this.address, this.port));
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Sending DatagramPacket");
        }
    }

}
