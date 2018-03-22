package com.DBS.Client;

import com.Network.Peer.PeerInterface;
import com.DBS.Protocol.*;

class Client {

    private PeerInterface peer;
    private Protocol protocol;

    public Client(String[] args) {
        String peer_access_point = args[0];
        String protocol_str = args[1];

        /*try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            peer = (PeerInterface) registry.lookup(peer_access_point);
        } catch (Exception e) {
            System.out.println("Couldn't connect to peer: " + peer_access_point);
        }*/

        switch (protocol_str) {
          case "BACKUP":
            String file_path = args[2];
            int replication_degree = Integer.parseInt(args[3]);
            protocol = new Backup(file_path, replication_degree);
            break;
          default:
            System.out.println("Invalid protocol");
            break;
        }
    }

    public void run() {
      protocol.run();
    }

    public static void main(String[] args) {
        Client client = new Client(args);
        client.run();
    }
}
