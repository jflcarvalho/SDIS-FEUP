package peers.Protocol;

import network.Network;
import peers.ChordNode;
import peers.Node;

import java.net.InetSocketAddress;
import java.net.Socket;

public class MessageHandler {
    public void handle(ChordNode node, Message msg, Socket socket){
        Node x;
        switch (msg.getType()) {
            case FINDSUCCESSOR:
                x = node.findSuccessor(msg.getNode());
                Network.send(socket, MessageFactory.ReplyFindSuccessor(x));
                break;
            case SET_PREDECESSOR:
                Node lastPred = node.notified(msg.getNode());
                if(lastPred != null)
                    Network.send(socket, MessageFactory.ReplySetPredecessor(lastPred));
                break;
            case GET_CLOSEST:
                Node closest = node.closestPrecedingFinger(msg.getNode());
                Network.send(socket, MessageFactory.ReplyGetCloset(closest));
                break;
            case GET_SUCCESSOR:
                x = node.getSuccessor();
                Network.send(socket, MessageFactory.ReplyGetSuccessor(x));
                break;
            case GET_PREDECCESSOR:
                x = node.getPredecessor();
                Network.send(socket, MessageFactory.ReplyGetPredeccessor(x));
                break;
            case UPDATE_FINGER:
                node.updateFingerTable(msg.getNode());
            default:
                break;
        }
    }
}
