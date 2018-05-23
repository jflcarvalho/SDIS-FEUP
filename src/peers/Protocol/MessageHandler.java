package peers.Protocol;

import peers.ChordNode;
import peers.Node;

import static peers.Protocol.Message.MessageType.FINDSUCCESSOR;

public class MessageHandler {
    public void handle(ChordNode node, Message msg){
        switch (msg.getType()) {
            case FINDSUCCESSOR:
                node.findSuccessor(new Node(msg.get_node()));
            default:
                break;
        }
    }
}
