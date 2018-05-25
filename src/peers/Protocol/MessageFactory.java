package peers.Protocol;

import java.net.InetSocketAddress;

import static peers.Protocol.Message.MessageType.*;

public abstract class MessageFactory {

    // TODO: create a message pool

    // Message to get successor of the node
    public static Message FindSuccessor(InetSocketAddress node) {
        return new Message(FINDSUCCESSOR, node);
    }

    public static Message ReplyFindSuccessor(InetSocketAddress node){
        return new Message(REPLY_FINDSUCCESSOR, node);
    }

    // Message to inform some peer that the node is your's predecessor
    public static Message SetPredecessor(InetSocketAddress node) {
        return new Message(SET_PREDECESSOR, node);
    }

    public static Message ReplySetPredecessor(InetSocketAddress node){ return new Message(REPLY_SETPREDECESSOR, node); }

    public static Message GetSuccessor(InetSocketAddress node){
        return new Message(GET_SUCCESSOR, node);
    }

    public static Message GetCloset(InetSocketAddress node){
        return new Message(GET_CLOSEST, node);
    }

    public static Message ReplyGetCloset(InetSocketAddress node){
        return new Message(REPLY_GETCLOSEST, node);
    }
}