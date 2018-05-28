package peers.Protocol;

import network.Network;
import peers.*;
import user.User;

import java.io.Serializable;
import java.net.Socket;
import java.util.HashSet;

import static peers.Protocol.Message.MessageType.*;

public class MessageHandler {
    public void handle(ChordNode node, Message msg, Socket socket){
        Node x;
        switch (msg.getType()) {
            case FIND_SUCCESSOR:
                x = node.findSuccessor(((ChordMessage) msg).getNode());
                msg = MessageFactory.getMessage(REPLY_FIND_SUCCESSOR, new Serializable[]{x});
                Network.send(socket, msg);
                break;
            case SET_PREDECESSOR:
                Node lastPred = node.notified(((ChordMessage) msg).getNode());
                if(lastPred != null) {
                    msg = MessageFactory.getMessage(REPLY_SET_PREDECESSOR, new Serializable[]{lastPred});
                    Network.send(socket, msg);
                }
                break;
            case GET_CLOSEST:
                Node closest = node.closestPrecedingFinger(((ChordMessage) msg).getNode());
                msg = MessageFactory.getMessage(REPLY_GET_CLOSEST, new Serializable[]{closest});
                Network.send(socket, msg);
                break;
            case GET_SUCCESSOR:
                x = node.getSuccessor();
                msg = MessageFactory.getMessage(REPLY_GET_SUCCESSOR, new Serializable[]{x});
                Network.send(socket, msg);
                break;
            case GET_PREDECESSOR:
                x = node.getPredecessor();
                msg = MessageFactory.getMessage(REPLY_GET_PREDECESSOR, new Serializable[]{x});
                Network.send(socket, msg);
                break;
            case UPDATE_FINGER:
                node.updateFingerTable(((ChordMessage) msg).getNode());
            default:
                break;
        }
    }

    public void handle(DatabaseManager node, Message msg, Socket socket) {
        User user;
        switch (msg.getType()) {
            case LOGIN:
                user = ((APIMessage) msg).getUser();
                msg = MessageFactory.getMessage(REPLY_LOGIN, new Serializable[]{user, node.login(user)});
                Network.send(socket, msg);
                break;
            case REGISTER:
                user = ((APIMessage) msg).getUser();
                msg = MessageFactory.getMessage(REPLY_LOGIN, new Serializable[]{user, node.register(user)});
                Network.send(socket, msg);
                break;
            case GET_LOGIN_DATA:
                msg = MessageFactory.getMessage(REPLY_LOGIN_DATA, new Serializable[]{null, node.getDataResponsibilities(((DatabaseMessage) msg).getNode())});
                Network.send(socket, msg);
                break;
            default:
                break;
        }
    }

    public void handle(ChordNode node, WorkerMessage msg, Socket socket) {
        switch (msg.getType()) {
            case SUBMIT:
                ((Worker) node).submit((Task) msg.getArg());
                break;
            case EXECUTE:
                ((Worker) node).execute((Task) msg.getArg());
                break;
            case SAVE_TASK:
                ((DatabaseManager) node).saveTask((Task) msg.getArg());
                break;
            case DELETE_TASK:
                ((Worker) node).deleteTask((Task) msg.getArg());
                break;
            case GET_TASKS:
                User user = (User) msg.getArg();
                HashSet<Task> tasks = ((DatabaseManager) node).getUserTasks(user);
                Message reply = MessageFactory.getMessage(REPLY_GET_TASKS, new Serializable[]{tasks});
                Network.send(socket, reply);
                break;
            default:
                break;
        }
    }
}
