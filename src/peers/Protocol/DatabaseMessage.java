package peers.Protocol;

import peers.Node;
import peers.Task;
import user.User;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class DatabaseMessage extends ChordMessage {
    private ConcurrentSkipListMap<Integer, User> login_Data;
    private ConcurrentHashMap<Integer, HashSet<Task>> tasks_Data;

    DatabaseMessage(MessageType type, Node node, ConcurrentSkipListMap<Integer, User> login_Data) {
        super(type, node);
        this.login_Data = login_Data;
    }

    public DatabaseMessage(MessageType type, Node node, ConcurrentHashMap<Integer, HashSet<Task>> tasks_Data) {
        super(type, node);
        this.tasks_Data = tasks_Data;
    }

    public ConcurrentSkipListMap<Integer, User> getLogin_Data() {
        return login_Data;
    }

    public ConcurrentHashMap<Integer, HashSet<Task>> getTasks_Data() { return tasks_Data; }
}
