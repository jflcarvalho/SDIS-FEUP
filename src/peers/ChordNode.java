package peers;

import network.Network;
import peers.Protocol.Message;
import peers.Protocol.MessageFactory;
import peers.Threads.ReceiveMessages;

import java.net.InetSocketAddress;
import java.util.Hashtable;

import static peers.Protocol.Message.MessageType.*;

public class ChordNode extends Node {
    private Hashtable<Integer, Node> finger_table = new Hashtable<>();
    private Node predecessor = null;
    private ReceiveMessages listener;

    public ChordNode(int node_Port) {
        super(node_Port);
        init();
    }

    private void init(){
        predecessor = this;
        for (int i = 1; i <= 32; i++){
            finger_table.put(i, this);
        }
        listener = new ReceiveMessages(this);
        new Thread(listener).start();
    }

    public Node getPredecessor() {
        return predecessor;
    }

    public Node getSuccessor(){
        return finger_table.get(1);
    }

    public boolean join (Node contact){
        if (contact == null || contact.equals(this)) {
            //TODO: start all threads
            return true;
        }

        // Find Successor node
        Message reply = Network.sendRequest(contact, MessageFactory.FindSuccessor(node_address), true);
        if(reply == null || reply.getType() != REPLY_FINDSUCCESSOR)
            return false;

        Node successor = new Node(reply.get_node());
        updateFingerTable(1, successor);

        predecessor = notify(successor);
        if(predecessor == null)
            return false;

        //TODO: start all threads
        return true;
    }

    public Node notify(Node successor){
        // Notify Successor that this node is the new predecessor
        Message reply = Network.sendRequest(successor, MessageFactory.SetPredecessor(node_address), true);
        if(reply == null || reply.getType() != REPLY_SETPREDECESSOR)
            return null;
        return new Node(reply.get_node());
    }

    public InetSocketAddress notified(Node node){
        InetSocketAddress ret = null;
        // Verify if is correct
        if(predecessor != null){
            ret = predecessor.getAddress();
        }
        if(getSuccessor().equals(this))
            updateFingerTable(1, node);
        predecessor = node;
        return ret;
    }

    public void updateFingerTable(int i, Node node){
        finger_table.put(i, node);
        for(i += 1; i < 32; i++){
            if(Integer.compareUnsigned(this.difference(finger_table.get(i)), this.difference(node)) < 0)
                finger_table.put(i, node);
            else return;
        }
    }

    public InetSocketAddress findSuccessor(Node node) {
        Node n = findPredecessor(node);

        if(this.compareTo(n) == 0)
            return getSuccessor().getAddress();
        Message reply = Network.sendRequest(n, MessageFactory.GetSuccessor(n.getAddress()), true);
        if(reply == null || reply.getType() != REPLY_GETSUCCESSOR)
            return null;

        return reply.get_node();
    }

    public Node findPredecessor(Node node){
        Node ret = this;
        Node successor = getSuccessor();
        if(this.compareTo(successor) == 0)
            return ret;
        while(Integer.compareUnsigned(ret.difference(node), ret.difference(successor)) > 0){
            if(ret.compareTo(this) == 0)
                ret = closestPrecedingFinger(node);
            else {
                Message reply = Network.sendRequest(ret, MessageFactory.GetCloset(node.getAddress()), true);
                if(reply == null || reply.getType() != REPLY_GETCLOSEST)
                    return null;
                ret = new Node(reply.get_node());
            }

            Message reply = Network.sendRequest(ret, MessageFactory.GetSuccessor(ret.getAddress()), true);
            if(reply == null || reply.getType() != REPLY_GETSUCCESSOR)
                return null;
            successor = new Node(reply.get_node());
        }
        return ret;
    }

    public Node closestPrecedingFinger(Node node){
        for (int i = 31; i >= 0; i--){
            Node closest_preceding = finger_table.get(i);
            if(Integer.compareUnsigned(this.difference(closest_preceding), this.difference(node)) < 0)
                return closest_preceding;
        }
        return null;
    }

}
