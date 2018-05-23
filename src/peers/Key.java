package peers;

import static utils.Utils.hashString;

public abstract class Key {
    public static Integer getUniqueKey(String node_IP, int node_Port) {
        byte[] hash = hashString(node_IP + Integer.toString(node_Port));
        if(hash == null)
            return null;
        return Integer.parseUnsignedInt(new String(hash));
    }
}
