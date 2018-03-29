package dbs.network;

import dbs.peer.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import static dbs.utils.Constants.PACKET_LENGTH;

public abstract class M_Channel implements Runnable {
    private MulticastSocket mc_socket;
    private InetAddress address;
    private int port;
    protected Peer peer;

    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @param peer    peer
     */
    M_Channel(String address, int port, Peer peer) {
        this.peer = peer;
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
        byte[] buffer = new byte[PACKET_LENGTH];

        DatagramPacket packet = new DatagramPacket(buffer, PACKET_LENGTH);

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
            new Thread(() -> handleRequest(packet)).start();
        }
    }

    protected abstract void handleRequest(DatagramPacket packet);

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
