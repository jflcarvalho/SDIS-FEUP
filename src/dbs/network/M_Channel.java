package dbs.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static dbs.utils.Constants.PACKETLENGHT;

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
    public M_Channel(String address, int port) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = port;
            System.out.println(this.getClass().getName() + " - " + address + ":" + port);

            mc_socket = new MulticastSocket(port);
            mc_socket.joinGroup(this.address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive incoming request
     *
     * @return DatagramPacket with request
     */
    private DatagramPacket receiveRequest(){
        byte[] buffer = new byte[PACKETLENGHT];

        DatagramPacket packet = new DatagramPacket(buffer, PACKETLENGHT);

        try {
            mc_socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public void send(byte[] packetBody) {
        try {
            mc_socket.send(new DatagramPacket(packetBody, packetBody.length, this.address, this.port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = this.receiveRequest();
            byte[] data = packet.getData();
            String string_message = new String(Arrays.copyOfRange(data, 0, packet.getLength()), StandardCharsets.US_ASCII);
            new Thread(() -> handleRequest(string_message)).start();
        }
    }

    abstract void handleRequest(String string_message);

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
