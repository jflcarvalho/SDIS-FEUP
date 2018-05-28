package peers.Protocol;

import peers.Node;
import user.User;
import java.util.concurrent.ConcurrentSkipListMap;

public class DatabaseMessage extends ChordMessage {
    private ConcurrentSkipListMap<Integer, User> login_Data;

    DatabaseMessage(MessageType type, Node node, ConcurrentSkipListMap<Integer, User> login_Data) {
        super(type, node);
        this.login_Data = login_Data;
    }

    public ConcurrentSkipListMap<Integer, User> getLogin_Data() {
        return login_Data;
    }
}
