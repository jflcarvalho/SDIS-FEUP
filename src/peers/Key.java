package peers;

import static utils.Utils.getHex;
import static utils.Utils.hashString;

public abstract class Key {
    public static Integer getUniqueKey(String node_IP, int node_Port) {
        String hexHash = node_IP + Integer.toString(node_Port);
        return hexHash.hashCode();
    }
}
