package peers;

import user.User;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface DataBasePeer {
    /** HashTable with all user infos to login */
    ConcurrentHashMap<Integer, User> loginHash = new ConcurrentHashMap<>();

    /** HashTable with all tasks from user */
    ConcurrentHashMap<String, HashSet<String>> userTasks = new ConcurrentHashMap<>();

    boolean login(User user);

    boolean register(User user);
}
