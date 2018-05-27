package peers;

import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

public interface ChordPeer {
    ConcurrentHashMap<Integer, Node> finger_table = new ConcurrentHashMap();

    boolean join (Node contact);

    Node notify(Node successor);

    Node notified(Node node);

    Node findSuccessor(Node node);

    Node closestPrecedingFinger(Node node);
}
