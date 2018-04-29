package database;

import java.util.HashSet;
import java.util.Hashtable;

public abstract class DataBase {
    /** HashTable with all user infos to login */
    private static Hashtable<String, String> loginHash = new Hashtable<>();

    /**
     * Logins Getter
     * @return Hattable of login infos
     */
    public static Hashtable<String, String> getLoginHash() {
        return loginHash;
    }

    //TODO: javadoc
    private static Hashtable<String, HashSet<String>> userTasks = new Hashtable<>();

    public static Hashtable<String, HashSet<String>> getUserTasks(){
        return userTasks;
    }
}
