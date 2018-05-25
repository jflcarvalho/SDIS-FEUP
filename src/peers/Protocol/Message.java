package peers.Protocol;

import java.io.Serializable;
import java.net.InetSocketAddress;


public class Message implements Serializable {

    public enum MessageType {
        FINDSUCCESSOR(0), REPLY_FINDSUCCESSOR(1), SET_PREDECESSOR(2), REPLY_SETPREDECESSOR(3), GET_SUCCESSOR(4), REPLY_GETSUCCESSOR(5), GET_CLOSEST(6), REPLY_GETCLOSEST(7);

        private int messageId;

        MessageType(int messageId){
            this.messageId= messageId;
        }

        public int getValue(){
            return this.messageId;
        }
    }

    private MessageType type;
    private InetSocketAddress _node;

    public Message(MessageType type, InetSocketAddress node) {
        this.type = type;
        _node = node;
    }

    public MessageType getType() {
        return type;
    }

    public InetSocketAddress get_node() {
        return _node;
    }
}
