package peers;

import network.Network;

import java.net.InetSocketAddress;
import java.util.Hashtable;

public class ChordNode extends Node {
    private Hashtable<Integer, InetSocketAddress> finger_table = new Hashtable<>();
    private InetSocketAddress predecessor;

    public ChordNode(int node_Port) {
        super(node_Port);
        init();
    }

    public ChordNode(String node_ip, int node_port) {
        super(node_ip, node_port);
    }

    private void init(){
        for (int i = 0; i < 32; i++){
            finger_table.put(i, null);
        }
    }

    public boolean join (InetSocketAddress contact){
        if (contact != null && !contact.equals(node_address)) {
            return true;
        }
        //TODO: Find successor
        Network.sendRequest(contact, "FINDSUCCESSOR_" + node_address.getAddress().getHostAddress() + ":" + node_address.getPort());
        //TODO: Notify successor
        return false;
    }

    public void notify(InetSocketAddress successor){
        if (successor!=null && !successor.equals(node_address))
            Network.sendRequest(successor, "PREDECESSOR_" +
                    node_address.getAddress().getHostAddress() + ":" + node_address.getPort());
    }

    public void notified(InetSocketAddress newPredecessor) {
        if(predecessor == null && predecessor.equals(node_address.getAddress()))
            predecessor = newPredecessor;
        else {
            Network.sendRequest(newPredecessor, "PREDECESSOR_" +
                    predecessor.getAddress().getHostAddress() + ":" + predecessor.getPort());
            predecessor = newPredecessor;
        }
    }

    public InetSocketAddress findSuccessor(String node_ID){
        return null;
    }

    public void updateFingerTable(int i, InetSocketAddress node){
        finger_table.put(i, node);
    }
}
