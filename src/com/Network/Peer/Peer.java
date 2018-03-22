package com.Network.Peer;

import java.rmi.RemoteException;

public class Peer implements PeerInterface{
    @Override
    public void backup(String file, int replicationDegree) throws RemoteException {

    }

    @Override
    public void restore(String file) throws RemoteException {

    }

    @Override
    public void delete(String file) throws RemoteException {

    }

    @Override
    public void reclaimSpace(int value) throws RemoteException {

    }

    @Override
    public void state() throws RemoteException {

    }
}
