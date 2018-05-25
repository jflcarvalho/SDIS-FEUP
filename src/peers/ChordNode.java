package peers;

import network.Network;
import peers.Protocol.Message;
import peers.Protocol.MessageFactory;
import peers.Threads.ReceiveMessages;

import java.net.InetSocketAddress;
import java.util.Hashtable;

import static peers.Protocol.Message.MessageType.*;

public class ChordNode extends Node {
    private Hashtable<Integer, InetSocketAddress> finger_table = new Hashtable<>();
    private Node predecessor = null;
    private ReceiveMessages listener;

    public ChordNode(int node_Port) {
        super(node_Port);
        init();
    }

    private void init(){
        predecessor = this;
        for (int i = 0; i < 32; i++){
            finger_table.put(i, node_address);
        }
        listener = new ReceiveMessages(this);
        new Thread(listener).start();
    }

    public Node getPredecessor() {
        return predecessor;
    }

    public InetSocketAddress getSuccessor(){
        return finger_table.get(1);
    }

    public boolean join (Node contact){
        if (contact != null && !contact.equals(this)) {
            //TODO: start all threads
            return true;
        }

        // Find Successor node
        Message reply = Network.sendRequest(contact, MessageFactory.FindSuccessor(node_address), true);
        if(reply == null || reply.getType() != REPLY_FINDSUCCESSOR)
            return false;

        Node successor = new Node(reply.get_node());
        updateFingerTable(1, successor.getAddress());

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

    public void notified(Node node){
        // Verify if is correct
        if(predecessor != null){
            Network.sendRequest(node, MessageFactory.ReplySetPredecessor(predecessor.getAddress()), false);
        }
        predecessor = node;
    }

    public void updateFingerTable(int i, InetSocketAddress node){
        finger_table.put(i, node);
    }

    public Node findSuccessor(Node node) {
        Node n = findPredecessor(node);

        Message reply = Network.sendRequest(n, MessageFactory.GetSuccessor(n.getAddress()), true);
        if(reply == null || reply.getType() != REPLY_GETSUCCESSOR)
            return null;
        Network.sendRequest(node, MessageFactory.ReplyFindSuccessor(reply.get_node()), false);
        return new Node(reply.get_node());
    }

    public Node findPredecessor(Node node){
        Node ret = this;
        Node successor = new Node(((ChordNode) ret).getSuccessor());
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
            Node closest_preceding = new Node(finger_table.get(i));
            if(Integer.compareUnsigned(this.difference(node), this.difference(closest_preceding)) < 0)
                return  closest_preceding;
        }
        return null;
    }

}
