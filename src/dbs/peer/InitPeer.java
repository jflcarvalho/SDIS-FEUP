package dbs.peer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static dbs.utils.Constants.DEBUG;

class InitPeer {

    public static void main(String[] args) {
        /* Needed for Mac OS X */
        System.setProperty("java.net.preferIPv4Stack", "true");

        Peer initiator_Peer = new Peer(args);

        try {
            System.out.println("Access Point: " + initiator_Peer.getAccessPoint());
            PeerInterface peerRMI = (PeerInterface) UnicastRemoteObject.exportObject(initiator_Peer, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(initiator_Peer.getAccessPoint(), peerRMI);
        } catch (Exception e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("Failed to bind peer to registry");
        }

        Runtime.getRuntime().addShutdownHook(new Thread(initiator_Peer::saveData));

        initiator_Peer.start();
    }
}
