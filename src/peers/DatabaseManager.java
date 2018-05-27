package peers;

import network.Network;
import peers.Protocol.APIMessage;
import peers.Protocol.Message;
import peers.Protocol.MessageFactory;
import user.User;

import java.io.Serializable;

import static peers.Protocol.Message.MessageType.*;
import static utils.Constants.MSG_LOGIN_USER_ALREADY_EXIST;
import static utils.Constants.MSG_LOGIN_USER_DOES_NOT_EXIST;
import static utils.Constants.MSG_LOGIN_WRONG_PASSWORD;
import static utils.Utils.exceptionPrint;

public class DatabaseManager extends ChordNode implements DataBasePeer {

    public DatabaseManager(int node_Port) {
        super(node_Port);
    }

    public boolean tryLogin(User user){
        int lookup_ID = user.hashCode();
        if(Integer.compareUnsigned(getPredecessor().node_ID, lookup_ID) < 0 && Integer.compareUnsigned(lookup_ID, node_ID) < 0){
            try {
                return login(user);
            } catch (User.LoginException e) {
                exceptionPrint(e, e.getError());
                return false;
            }
        } else {
            Node n = findSuccessor(new Node(lookup_ID, null));
            Message msg = MessageFactory.getMessage(LOGIN, new Serializable[]{user});
            msg = Network.sendRequest(n, msg, true);
            if(msg != null && msg.getType() == REPLY_LOGIN)
                return ((APIMessage) msg).getReplyValue();
        }
        return false;
    }

    @Override
    public boolean login(User user) throws User.LoginException {
        if(!loginHash.containsKey(user.getUsername()))
            throw new User.LoginException(MSG_LOGIN_USER_DOES_NOT_EXIST);
        if(!loginHash.get(user.getUsername()).equals(user.getPassword()))
            throw new User.LoginException(MSG_LOGIN_WRONG_PASSWORD);
        System.out.println("User Login");
        return true;
    }

    public boolean tryRegister(User user){
        if(user == null)
            return false;
        int lookup_ID = user.hashCode();
        if(Integer.compareUnsigned(getPredecessor().node_ID, lookup_ID) < 0 && Integer.compareUnsigned(lookup_ID, node_ID) < 0){
            try {
                return register(user);
            } catch (User.LoginException e) {
                exceptionPrint(e, e.getError());
                return false;
            }
        } else {
            Node n = findSuccessor(new Node(lookup_ID, null));
            Message msg = MessageFactory.getMessage(REGISTER, new Serializable[]{user});
            msg = Network.sendRequest(n, msg, true);
            if(msg != null && msg.getType() == REPLY_REGISTER)
                return ((APIMessage) msg).getReplyValue();
        }
        return false;
    }

    @Override
    public boolean register(User user) throws User.LoginException {
        if(loginHash.containsKey(user.getUsername()))
            throw new User.LoginException(MSG_LOGIN_USER_ALREADY_EXIST);
        loginHash.put(user.getUsername(), user.getPassword());
        System.out.println("New user register");
        return true;
    }
}
