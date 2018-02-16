package com.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class Client {
    private InetAddress address;
    private DatagramSocket socket;

    public Client(String hostname, int port, String data) {
        try{
            socket = new DatagramSocket();
            address = InetAddress.getByName(hostname);
            byte[] dataBuffer = data.getBytes();
            DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length, address, port);
            System.out.println("Sending: " + new String(packet.getData()));
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finalize() {
      socket.close();
    }
}
