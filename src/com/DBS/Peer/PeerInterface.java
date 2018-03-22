package com.DBS.Peer;



import java.rmi.Remote;
import java.rmi.RemoteException;


public interface PeerInterface extends Remote {

    void backup(String file, int replicationDegree) throws RemoteException;

    void restore(String file) throws RemoteException;

    void delete(String file) throws RemoteException;

    void reclaimSpace(int value) throws RemoteException;

    void state() throws RemoteException;
}