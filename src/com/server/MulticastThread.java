package com.server;

import java.lang.Thread;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.InetAddress;

class MulticastThread extends Thread{

    private String multicast_address;
    private String service_address;
    private int multicast_port;
    private int service_port;

    private MulticastSocket socket;
    InetAddress multicast_inetaddress;

    public MulticastThread(String multicast_address, int multicast_port, int service_port) {
      super();
      this.multicast_address = multicast_address;
      this.multicast_port = multicast_port;
      this.service_port = service_port;
      try {
        this.service_address = InetAddress.getLocalHost().getHostAddress();
        this.multicast_inetaddress = InetAddress.getByName(multicast_address);
        socket = new MulticastSocket();
        socket.setTimeToLive(1);
      } catch (Exception e) {
        System.out.println("Couldn't create Multicast Thread");
      }
    }

    public void run() {
        System.out.println("Advertisement Thread Running");

        try {
          while(true) {
            String advertisement = service_address + ":" + Integer.toString(service_port);
            DatagramPacket packet = new DatagramPacket(advertisement.getBytes(), advertisement.getBytes().length, multicast_inetaddress, multicast_port);
            socket.send(packet);
            System.out.println("Sent advertisement message: " + advertisement);
            Thread.sleep(1000);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    @Override
    public void finalize() {
      socket.close();
    }
}
