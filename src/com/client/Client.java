package com.client;

import java.io.IOException;
import java.net.*;

public class Client {
    private InetAddress address;
    private byte[] dataBuffer;

    private DatagramSocket socket;
    private DatagramPacket packet;

    public Client(String hostname, int port, String data) {
        try{
            socket = new DatagramSocket();
            address = InetAddress.getByName(hostname);
            dataBuffer = data.getBytes();
            packet = new DatagramPacket(dataBuffer, dataBuffer.length, address, port);
            System.out.println("Sending: " + new String(packet.getData()));
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
      }
}
