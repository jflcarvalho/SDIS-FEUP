package dbs.client;

import dbs.peer.PeerInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static dbs.utils.Constants.DEBUG;

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
            default:
                System.out.println("Protocol does don't exists");
        }
    }

    private void startBackup(){
        try {
            peer.backup(file_path, replication_degree);
        } catch (RemoteException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Starting Backup Protocol");
        }
    }

    private void startRestore(){
        try {
            peer.restore(file_path);
        } catch (RemoteException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Starting Restore Protocol");
        }
    }

    private void startDelete(){
        try {
            peer.delete(file_path);
        } catch (RemoteException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Starting Delete Protocol");
        }
    }

    private void startSpaceReclaim(){
        try {
            peer.reclaimSpace(space_Reclaim);
        } catch (RemoteException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Starting Space Reclaim Protocol");
        }
    }

    private void startState(){
        try {
            System.out.print(peer.state());
        } catch (RemoteException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Starting State Protocol");
        }
    }
}
