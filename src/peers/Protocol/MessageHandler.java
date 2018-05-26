package peers.Protocol;

import network.Network;
import peers.ChordNode;
import peers.Node;

import java.net.InetSocketAddress;
import java.net.Socket;

public class MessageHandler {
    public void handle(ChordNode node, Message msg, Socket socket){
        InetSocketAddress successor;
        switch (msg.getType()) {
            case FINDSUCCESSOR:
                successor = node.findSuccessor(new Node(msg.get_node()));
                Network.send(socket, MessageFactory.ReplyFindSuccessor(successor));
                break;
            case SET_PREDECESSOR:
                InetSocketAddress lastPred = node.notified(new Node(msg.get_node()));
                if(lastPred != null)
                    Network.send(socket, MessageFactory.ReplySetPredecessor(lastPred));
                break;
            case GET_CLOSEST:
                Node closest = node.closestPrecedingFinger(new Node(msg.get_node()));
                Network.send(socket, MessageFactory.ReplyGetCloset(closest.getAddress()));
                break;
            case GET_SUCCESSOR:
                successor = node.getSuccessor().getAddress();
                Network.send(socket, MessageFactory.ReplyGetSuccessor(successor));
                break;
            default:
                break;
        }
    }
}
