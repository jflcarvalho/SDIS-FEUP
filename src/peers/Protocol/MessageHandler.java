package peers.Protocol;

import network.Network;
import peers.ChordNode;
import peers.Node;

import java.net.Socket;

public class MessageHandler {
    public void handle(ChordNode node, Message msg, Socket socket){
        switch (msg.getType()) {
            case FINDSUCCESSOR:
                node.findSuccessor(new Node(msg.get_node()));
            case SET_PREDECESSOR:
                node.notified(new Node(msg.get_node()));
            case GET_CLOSEST:
                Node closest = node.closestPrecedingFinger(new Node(msg.get_node()));
                Network.send(socket, MessageFactory.ReplyGetCloset(closest.getAddress()));
            default:
                break;
        }
    }
}
