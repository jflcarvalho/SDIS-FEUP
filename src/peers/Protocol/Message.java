package peers.Protocol;

import peers.Node;

import java.io.Serializable;


public class Message implements Serializable {

    public enum MessageType {
        FINDSUCCESSOR(0), REPLY_FINDSUCCESSOR(1), SET_PREDECESSOR(2), REPLY_SETPREDECESSOR(3),
        GET_SUCCESSOR(4), REPLY_GETSUCCESSOR(5), GET_CLOSEST(6), REPLY_GETCLOSEST(7),
        GET_PREDECCESSOR(8), REPLY_GETPREDECCESSOR(9), UPDATE_FINGER(10);

        private int messageId;

        MessageType(int messageId){
            this.messageId= messageId;
        }

        public int getValue(){
            return this.messageId;
        }
    }

    private MessageType type;
    private Node _node;

    public Message(MessageType type, Node node) {
        this.type = type;
        _node = node;
    }

    public MessageType getType() {
        return type;
    }

    public Node getNode() {
        return _node;
    }
}
