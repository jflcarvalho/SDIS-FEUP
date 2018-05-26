package peers;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import static peers.Key.getUniqueKey;
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
            this.node_ID = getUniqueKey(node_IP.getHostAddress(), node_Port);
        } catch (UnknownHostException e) {
            exceptionPrint(e, "[ERROR] Fail Creating Node");
        }
    }

    public Node(String node_ip, int node_port){
        init(node_ip, node_port);
    }

    public Node(String address){
        String[] replySplited = address.split(":");
        boolean valid_IP = Pattern.matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}", replySplited[0]);
        boolean valid_port = Pattern.matches("\\d{1,5}", replySplited[1]);

        if(valid_IP && valid_port)
            init(replySplited[0], Integer.parseInt(replySplited[1]));
    }

    public Node(InetSocketAddress address){
        init(address.getAddress().getHostAddress(), address.getPort());
    }

    private void init(String node_ip, int node_port){
        try {
            InetAddress node_IP = InetAddress.getByName(node_ip);
            node_address = new InetSocketAddress(node_IP, node_port);
            node_ID = getUniqueKey(node_IP.getHostAddress(), node_port);
        } catch (UnknownHostException e) {
            exceptionPrint(e, "[ERROR] Fail Creating Node");
        }
    }

    public void print(){
        System.out.println("Node_ID: " + node_ID);
        System.out.println("Node Address: " + node_address.getAddress().getHostAddress() + ":" + node_address.getPort());
    }

    public Integer getNode_ID() {
        return node_ID;
    }

    public InetSocketAddress getAddress() {
        return node_address;
    }

    public String getStringAddress(){
        return node_address.getAddress().getHostAddress() + ":" + node_address.getPort();
    }

    public String getIP(){
        return node_address.getAddress().getHostAddress();
    }

    public int getPort(){
        return node_address.getPort();
    }

    public int compareTo(Node o){
        return Integer.compareUnsigned(node_ID, o.getNode_ID());
    }

    public int difference(Node o){
        return (int) (Integer.toUnsignedLong(o.node_ID) - Integer.toUnsignedLong(node_ID));
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
