package peers;

import network.Network;
import peers.Protocol.ChordMessage;
import peers.Protocol.Message;
import peers.Protocol.MessageFactory;
import peers.Threads.ReceiveMessages;
import peers.Threads.Stabilizer;

import java.io.Serializable;

import static peers.Protocol.Message.MessageType.*;

public class ChordNode extends Node implements ChordPeer, Serializable {
    private Node _predecessor = null;
    private ReceiveMessages listener;
    private Stabilizer stabilizer;
    private Node _node;

    public ChordNode(int node_Port) {
        super(node_Port);
        init();
    }

    private void init(){
        _node = new Node(node_ID, node_address);
        _predecessor = _node;
        for (int i = 1; i <= 32; i++){
            finger_table.put(i, _node);
        }
        new Thread(listener = new ReceiveMessages(this)).start();
    }

    protected Node getNode() {
        return _node;
    }

    public Node getPredecessor() {
        return _predecessor;
    }

    public Node getSuccessor(){
        return finger_table.get(1);
    }

    public Node setPredecessor(Node predecessor){
        if(predecessor == null)
            return null;

        Node ret = _predecessor;
        if(_predecessor != null && Integer.compareUnsigned(_predecessor.difference(predecessor), _predecessor.difference(_node)) < 0){
            _predecessor = predecessor;
            for(int i = 32; i > 0; i--){
                if(finger_table.get(i).equals(this))
                    finger_table.put(i, _predecessor);
                else break;
            }
        }
        return ret;
    }

    public int updateFingerTable(int i, Node node) {
        int last_index = i;
        for(; i <= 32; i++){
            if(finger_table.get(i).equals(node))
                continue;
            Node idealNode = new Node(this.node_ID + (int) Math.pow(2,i-1), null);
            if(Integer.compareUnsigned(this.difference(idealNode), this.difference(node)) < 0) {
                finger_table.put(i, node);
                last_index = i;
            }
            else return last_index;
        }
        return 33;
    }

    public int updateFingerTable(Node node) {
        int diff = this.difference(node);
        int index = (int) (Math.log(diff) / Math.log(2));
        for(int i = index; i > 0; i--){
            if(Integer.compareUnsigned(this.difference(finger_table.get(i)), diff) > 0){
                finger_table.put(i, node);
            }
            else return i;
        }
        return 0;
    }

    @Override
    public boolean join (Node contact) {
        System.out.println("Joining to Network...");
        if (contact == null || contact.equals(this)) {
            new Thread(stabilizer = new Stabilizer(this)).start();
            return true;
        }
        Message msg = MessageFactory.getMessage(FIND_SUCCESSOR, new Serializable[]{this.getNode()});
        ChordMessage reply = (ChordMessage) Network.sendRequest(contact, msg, true);
        if(reply == null || reply.getType() != REPLY_FIND_SUCCESSOR)
            return false;

        Node successor = reply.getNode();
        _predecessor = notify(successor);
        if(_predecessor == null)
            return false;

        System.out.println("Successor: " + successor.node_ID + " - " + successor.getStringAddress());
        System.out.println("Predecessor: " + _predecessor.node_ID + " - " + _predecessor.getStringAddress());
        initFingerTable(successor);
        updateOthersFinger();

        new Thread(stabilizer = new Stabilizer(this)).start();
        return true;
    }

    @Override
    public Node notify(Node successor) {
        // Notify Successor that this node is the new _predecessor
        Message msg = MessageFactory.getMessage(SET_PREDECESSOR, new Serializable[]{this.getNode()});
        ChordMessage reply = (ChordMessage) Network.sendRequest(successor, msg, true);
        if(reply == null || reply.getType() != REPLY_SET_PREDECESSOR)
            return null;
        return reply.getNode();
    }

    @Override
    public Node notified(Node node) {
        if(getSuccessor().equals(this))
            updateFingerTable(1, node);

        return setPredecessor(node);
    }

    @Override
    public Node findSuccessor(Node node) {
        Node n = findPredecessor(node);

        if(this.compareTo(n) == 0)
            return getSuccessor();
        Message msg = MessageFactory.getMessage(GET_SUCCESSOR, new Serializable[]{n});
        ChordMessage reply = (ChordMessage) Network.sendRequest(n, msg, true);
        if(reply == null || reply.getType() != REPLY_GET_SUCCESSOR)
            return null;

        return reply.getNode();
    }

    private Node findPredecessor(Node node){
        Node ret = this.getNode();
        Node successor = getSuccessor();
        if(this.compareTo(successor) == 0)
            return ret;
        while(Integer.compareUnsigned(ret.difference(node), ret.difference(successor)) > 0){
            if(ret.compareTo(this) == 0)
                ret = closestPrecedingFinger(node);
            else {
                Message msg = MessageFactory.getMessage(GET_CLOSEST, new Serializable[]{node});
                ChordMessage reply = (ChordMessage) Network.sendRequest(ret, msg, true);
                if(reply == null || reply.getType() != REPLY_GET_CLOSEST)
                    return null;
                ret = reply.getNode();
            }
            Message msg = MessageFactory.getMessage(GET_SUCCESSOR, new Serializable[]{ret});
            ChordMessage reply = (ChordMessage) Network.sendRequest(ret, msg, true);
            if(reply == null || reply.getType() != REPLY_GET_SUCCESSOR)
                return null;
            successor = reply.getNode();
        }
        return ret;
    }

    @Override
    public Node closestPrecedingFinger(Node node){
        if(this.compareTo(node) == 0)
            return _predecessor;
        for (int i = 32; i > 0; i--){
            Node closest_preceding = finger_table.get(i);
            if(Integer.compareUnsigned(this.difference(closest_preceding), this.difference(node)) < 0)
                return closest_preceding;
        }
        return null;
    }

    protected void initFingerTable(Node successor) {
        int i = updateFingerTable(1, successor);
        for(; i <= 32; i++){
            Node idealNode = new Node(this.node_ID + (int) Math.pow(2,i-1), null);
            Message msg = MessageFactory.getMessage(FIND_SUCCESSOR, new Serializable[]{idealNode});
            ChordMessage reply = (ChordMessage) Network.sendRequest(successor, msg, true);
            if(reply == null || reply.getType() != REPLY_FIND_SUCCESSOR)
                continue;
            System.out.print("Find: " + idealNode.node_ID + " reply: " + reply.getNode().node_ID);
            i = updateFingerTable(i, reply.getNode());
            System.out.println(" index: " + i);
            // if produce error something is wrong because never should be -1
        }
    }

    private void updateOthersFinger() {
        for(int i = 1; i <= 32; i++){
            Node idealNode = new Node(this.node_ID - (int) Math.pow(2,i-1), null);
            Node predeccessorN = findPredecessor(idealNode);
            Message msg = MessageFactory.getMessage(UPDATE_FINGER, new Serializable[]{_node});
            Network.sendRequest(predeccessorN, msg, false);
        }
    }

    public String fingerToString(){
        StringBuilder ret = new StringBuilder();
        Node x = finger_table.get(1);
        ret.append("| [ ").append(1).append(" - ");
        for (int i = 2; i <= 9; i++){
            Node y = finger_table.get(i);
            if(!x.equals(y)){
                ret.append(i).append(") | ").append(x.getNode_ID()).append(" - ").append(x.getStringAddress()).append("|\n");
                ret.append("| [ ").append(i).append(" - ");
                x = y;
            }
        }
        for (int i = 10; i <= 32; i++){
            Node y = finger_table.get(i);
            if(!x.equals(y)){
                ret.append(i).append(") | ").append(x.getNode_ID()).append(" - ").append(x.getStringAddress()).append("|\n");
                ret.append("| [").append(i).append(" - ");
                x = y;
            }
        }

        ret.append("32] | ").append(x.getNode_ID()).append(" - ").append(x.getStringAddress()).append("|\n");
        return ret.toString();
    }

}
