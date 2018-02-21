package com.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class Client {
    private final int MAX_PACKET_LENGTH = 256;

    public Client(String multicast_address_str, int multicast_port, String data) {
        try{
          //Init
          MulticastSocket multicast_socket = new MulticastSocket();
          System.out.println("MULTICAST_ADDRESS: " + multicast_address_str);
          InetAddress multicast_address = InetAddress.getByAddress(multicast_address_str.getBytes());
          multicast_socket.joinGroup(multicast_address);
          DatagramSocket socket = new DatagramSocket();

          //Receive service info
          DatagramPacket service_info = new DatagramPacket(new byte[MAX_PACKET_LENGTH], MAX_PACKET_LENGTH, multicast_address, multicast_port);
          socket.receive(service_info);
          String service_info_info = new String(service_info.getData());
          service_info_info = service_info_info.substring(0, service_info_info.indexOf("\0"));
          System.out.println("Received: " + service_info_info);
/*
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
*/

          multicast_socket.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
}
