package peers.Protocol;

import peers.Node;

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
