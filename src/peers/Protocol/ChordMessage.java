package peers.Protocol;

import peers.Node;
import user.User;

import java.util.concurrent.ConcurrentSkipListMap;

public class ChordMessage extends Message {
    private Node _node;

    ChordMessage(MessageType type, Node node) {
        super(type);
        _node = node;
    }

    public Node getNode() {
        return _node;
    }
}
