package com.server;

import com.common.LicensePlate;
import com.common.Owner;

import java.util.HashMap;
import java.lang.String;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.DatagramPacket;

public class Server {
  private HashMap<LicensePlate,Owner> database;
  private DatagramSocket socket;

  private final int PACKET_LENGTH = 128;

  public Server(int port) {
    database = new HashMap<LicensePlate,Owner>();
    try {
      socket = new DatagramSocket(port);
    } catch (SocketException e) {
      System.out.println("Couldn't create socket");
    }
  }

  public void ProcessRequest(String request) {
    System.out.println(request);
  }

  public void Run() {
    while(true) {
      DatagramPacket packet = new DatagramPacket(new byte[PACKET_LENGTH], PACKET_LENGTH);
      socket.receive(packet);
      ProcessRequest(packet.getData());
    }
  }

  @Override
  public void finalize() {
    socket.close();
  }

  public static void main(String[] args) {
    Server server = new Server(80);
    server.Run();
    //server.ProcessRequest("REGISTER <plate number> <owner name>");
    //server.ProcessRequest("LOOKUP <plate number>");
  }
}
