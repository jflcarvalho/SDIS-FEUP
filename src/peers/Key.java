package peers;

import static utils.Utils.getHex;
import static utils.Utils.hashString;

public abstract class Key {
    public static Integer getUniqueKey(String node_IP, int node_Port) {
        String hexHash = Integer.toString(node_Port) + node_IP;
        return hexHash.hashCode();
    }
}
