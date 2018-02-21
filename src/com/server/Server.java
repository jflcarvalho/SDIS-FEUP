package com.server;

import com.common.LicensePlate;
import com.common.Owner;
import com.server.MulticastThread;

import java.util.HashMap;
import java.util.Map.Entry;
import java.lang.String;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class Server {
  private HashMap<LicensePlate,Owner> database;
  private DatagramSocket socket;

  private final int MAX_PACKET_LENGTH = 256;

  public Server(int port) {
    MulticastThread multicast_thread = new MulticastThread();
    multicast_thread.start();
    database = new HashMap<LicensePlate,Owner>();
    try {
      socket = new DatagramSocket(port);
    } catch (SocketException e) {
      System.out.println("Couldn't create socket");
    }
  }

  public void ProcessRequest(DatagramPacket packet) {
    String request = new String(packet.getData());
    request = request.substring(0, request.indexOf("\0"));
    String[] elements = request.split(" ");
    LicensePlate license_plate;
    InetAddress address = packet.getAddress();
    int port = packet.getPort();
    DatagramPacket response;
    String response_str;

    System.out.println("Received: " + request);
    if(elements.length > 1) {
      license_plate = new LicensePlate(elements[1]);
      if(elements[0].equals("REGISTER")) {
        if(database.containsKey(license_plate)) {
          response_str = Integer.toString(-1);
        } else {
          Owner owner = new Owner(elements[2]);
          database.put(license_plate, owner);
          response_str = Integer.toString(database.size());
        }
      } else if(elements[0].equals("LOOKUP")) {
        Owner owner = database.get(license_plate);
        if(owner == null)
          response_str = new String("NOT_FOUND");
        else response_str = owner.getName();
      } else response_str = new String("INVALID OPERATION");
    } else response_str = new String("INVALID OPERATION");

    try {
      byte[] data_buffer = response_str.getBytes();
      response = new DatagramPacket(data_buffer, data_buffer.length, address, port);
      System.out.println("Sending: " + response_str);
      socket.send(response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void Run() {
    while(true) {
      DatagramPacket packet = new DatagramPacket(new byte[MAX_PACKET_LENGTH], MAX_PACKET_LENGTH);
      try {
        socket.receive(packet);
        ProcessRequest(packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void finalize() {
    socket.close();
  }
}
