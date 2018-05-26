package peers.Protocol;

import peers.Node;

import java.net.InetSocketAddress;

import static peers.Protocol.Message.MessageType.*;

public abstract class MessageFactory {

    // TODO: create a message pool

    // Message to get successor of the node
    public static Message FindSuccessor(Node node) {
        return new Message(FINDSUCCESSOR, node);
    }

    public static Message ReplyFindSuccessor(Node node) {
        return new Message(REPLY_FINDSUCCESSOR, node);
    }

    // Message to inform some peer that the node is your's predecessor
    public static Message SetPredecessor(Node node) {
        return new Message(SET_PREDECESSOR,  node);
    }

    public static Message ReplySetPredecessor(Node node) {
        return new Message(REPLY_SETPREDECESSOR, node);
    }

    public static Message GetSuccessor(Node node) {
        return new Message(GET_SUCCESSOR, node);
    }

    public static Message ReplyGetSuccessor(Node node) {
        return new Message(REPLY_GETSUCCESSOR, node);
    }

    public static Message GetCloset(Node node) {
        return new Message(GET_CLOSEST, node);
    }

    public static Message ReplyGetCloset(Node node) {
        return new Message(REPLY_GETCLOSEST, node);
    }

    public static Message GetPredeccessor(Node node) {
        return new Message(GET_PREDECCESSOR, node);
    }

    public static Message ReplyGetPredeccessor(Node node) {
        return new Message(REPLY_GETPREDECCESSOR, node);
    }
}