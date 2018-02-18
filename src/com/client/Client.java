package com.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class Client {
    private InetAddress address;
    private DatagramSocket socket;

    private final int MAX_PACKET_LENGTH = 256;

    public Client(String hostname, int port, String data) {
        try{
          //Init
          socket = new DatagramSocket();
          address = InetAddress.getByName(hostname);

          //Send request
          byte[] dataBuffer = data.getBytes();
          DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length, address, port);
          System.out.println("Sending: " + data);
          socket.send(packet);

          //Receive response
          DatagramPacket response = new DatagramPacket(new byte[MAX_PACKET_LENGTH], MAX_PACKET_LENGTH, address, port);
          socket.receive(response);
          String response_str = new String(response.getData());
          response_str = response_str.substring(0, response_str.indexOf("\0"));
          System.out.println("Received: " + response_str);

        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    @Override
    public void finalize() {
      socket.close();
    }
}
