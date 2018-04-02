package dbs.client;

import dbs.peer.PeerInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class TestApp {

    private PeerInterface peer;
    private final String protocol;
    private String file_path;
    private int replication_degree;
    private int space_Reclaim;

    private TestApp(String[] args) {
        String peer_access_point = args[0];
        protocol = args[1];

        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            peer = (PeerInterface) registry.lookup(peer_access_point);
        } catch (Exception e) {
            System.out.println("Couldn't connect to peer: " + peer_access_point);
        }

        if(protocol.equals("SPACERECLAIM"))
            space_Reclaim = Integer.parseInt(args[2]);
        else if (!protocol.equals("STATE")){
            file_path = args[2];
            if(protocol.equals("BACKUP"))
                replication_degree = Integer.parseInt(args[3]);
        }
    }

    public static void main(String[] args) {
        TestApp client = new TestApp(args);
        switch (client.protocol){
            case "BACKUP":
                client.startBackup();
                break;
            case "RESTORE":
                client.startRestore();
                break;
            case "DELETE":
                client.startDelete();
                break;
            case "SPACERECLAIM":
                client.startSpaceReclaim();
                break;
            case "STATE":
                client.startState();
                break;
        }
    }

    private void startBackup(){
        try {
            peer.backup(file_path, replication_degree);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startRestore(){
        try {
            peer.restore(file_path);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startDelete(){
        try {
            peer.delete(file_path);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startSpaceReclaim(){
        try {
            peer.reclaimSpace(space_Reclaim);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startState(){
        try {
            peer.state();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
