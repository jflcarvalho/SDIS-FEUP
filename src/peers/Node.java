package peers;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static utils.Utils.exceptionPrint;
import static utils.Utils.hashString;

public class Node implements Serializable {
    protected Integer node_ID;
    protected InetSocketAddress node_address;

    public Node(int node_Port) {
        InetAddress node_IP;
        try {
            node_IP = InetAddress.getLocalHost();
            node_address = new InetSocketAddress(node_IP, node_Port);
            this.node_ID = getUniqueNodeID(node_IP.getHostAddress(), node_Port);
        } catch (UnknownHostException e) {
            exceptionPrint(e, "[ERROR] Fail Creating Node");
        }
    }

    public Node(String node_ip, int node_port){
        try {
            InetAddress node_IP = InetAddress.getByName(node_ip);
            node_address = new InetSocketAddress(node_IP, node_port);
            node_ID = getUniqueNodeID(node_IP.getHostAddress(), node_port);
        } catch (UnknownHostException e) {
            exceptionPrint(e, "[ERROR] Fail Creating Node");
        }
    }

    public static Integer getUniqueNodeID(String node_IP, int node_Port){
        byte[] hash = hashString(node_IP + Integer.toString(node_Port));
        if(hash == null)
            return null;
        return Integer.parseUnsignedInt(new String(hash));
    }

    public Integer getNode_ID() {
        return node_ID;
    }

    public String getNode_IP(){
        return node_address.getAddress().getHostAddress();
    }

    public int getNode_ip(){
        return node_address.getPort();
    }

    public int compareTo(Node o){
        return Integer.compareUnsigned(node_ID, o.getNode_ID());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!Node.class.isAssignableFrom(o.getClass())) {
            return false;
        }

        final Node node = (Node) o;
        return node_ID.equals(node.getNode_ID());
    }
}
