package peers;

import user.User;

import java.util.HashSet;
import java.util.Hashtable;

public interface DataBasePeer {
    /** HashTable with all user infos to login */
    Hashtable<String, String> loginHash = new Hashtable<>();

    /** HashTable with all tasks from user */
    Hashtable<String, HashSet<String>> userTasks = new Hashtable<>();

    boolean login(User user) throws User.LoginException;

    boolean register(User user) throws User.LoginException;
}
