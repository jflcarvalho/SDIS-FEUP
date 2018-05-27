package peers;

import network.Network;
import peers.Protocol.*;
import user.User;

import java.io.Serializable;
import java.util.concurrent.ConcurrentSkipListMap;

import static peers.Protocol.Message.MessageType.*;
import static utils.Constants.MSG_LOGIN_USER_ALREADY_EXIST;
import static utils.Constants.MSG_LOGIN_USER_DOES_NOT_EXIST;
import static utils.Constants.MSG_LOGIN_WRONG_PASSWORD;
import static utils.Utils.exceptionPrint;

public class DatabaseManager extends ChordNode implements DataBasePeer {
    private ConcurrentSkipListMap<Integer, User> lookupLogin = new ConcurrentSkipListMap<>();

    public DatabaseManager(int node_Port) {
        super(node_Port);
    }

    private boolean tryLogin(User user) throws User.LoginException {
        if(!loginHash.containsKey(user.getLookup()))
            throw new User.LoginException(MSG_LOGIN_USER_DOES_NOT_EXIST);
        if(!loginHash.get(user.getLookup()).getPassword().equals(user.getPassword()))
            throw new User.LoginException(MSG_LOGIN_WRONG_PASSWORD);
        System.out.println("User Login");
        return true;

    }

    @Override
    public boolean login(User user){
        Node lookup_Node = new Node(user.getLookup(), null);
        if(Integer.compareUnsigned(getPredecessor().difference(lookup_Node), getPredecessor().difference(getNode())) < 0 ){
            try {
                return tryLogin(user);
            } catch (User.LoginException e) {
                exceptionPrint(e, e.getError());
                return false;
            }
        } else {
            Node n = findSuccessor(lookup_Node);
            Message msg = MessageFactory.getMessage(LOGIN, new Serializable[]{user});
            msg = Network.sendRequest(n, msg, true);
            if(msg != null && msg.getType() == REPLY_LOGIN)
                return ((APIMessage) msg).getReplyValue();
        }
        return false;
    }

    private boolean tryRegister(User user) throws User.LoginException {
        if(loginHash.containsKey(user.getLookup()))
            throw new User.LoginException(MSG_LOGIN_USER_ALREADY_EXIST);
        loginHash.put(user.getLookup(), user);
        new Thread(() -> lookupLogin.put(user.getLookup(), user)).start();
        System.out.println("New user register");
        return true;
    }

    @Override
    public boolean register(User user) {
        if(user == null)
            return false;
        Node lookup_Node = new Node(user.getLookup(), null);
        if(Integer.compareUnsigned(getPredecessor().difference(lookup_Node), getPredecessor().difference(getNode())) < 0 ){
            try {
                return tryRegister(user);
            } catch (User.LoginException e) {
                exceptionPrint(e, e.getError());
                return false;
            }
        } else {
            Node n = findSuccessor(lookup_Node);
            Message msg = MessageFactory.getMessage(REGISTER, new Serializable[]{user});
            msg = Network.sendRequest(n, msg, true);
            if(msg != null && msg.getType() == REPLY_REGISTER)
                return ((APIMessage) msg).getReplyValue();
        }
        return false;
    }

    @Override
    protected void initFingerTable(Node successor) {
        super.initFingerTable(successor);
        Message msg = MessageFactory.getMessage(GET_LOGIN_DATA, new Serializable[]{successor, null});
        msg = Network.sendRequest(successor, msg, true);
        ConcurrentSkipListMap<Integer, User> data = ((DatabaseMessage) msg).getLogin_Data();
        loginHash.putAll(data);
        new Thread(() -> lookupLogin.putAll(data)).start();
    }

    public ConcurrentSkipListMap<Integer, User> getDataResponsibilities(Node predecessor){
        ConcurrentSkipListMap<Integer, User> ret = new ConcurrentSkipListMap<>(lookupLogin.subMap(0, predecessor.node_ID));
        lookupLogin = new ConcurrentSkipListMap<>(lookupLogin.subMap(predecessor.node_ID,true,  this.node_ID, true));
        return ret;
    }

}
