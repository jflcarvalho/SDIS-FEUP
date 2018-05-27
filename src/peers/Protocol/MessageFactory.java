package peers.Protocol;

import peers.Node;
import user.User;

import java.io.Serializable;


public abstract class MessageFactory {
    // TODO: create a message pool
    public static Message getMessage(Message.MessageType type, Serializable[] args){
        if(type.getValue() >= 0 && type.getValue() < 11 && args.length == 1)
            return new ChordMessage(type, (Node) args[0]);
        else if(type.getValue() >= 11 && type.getValue() < 15) {
            if(args.length == 1)
                return new APIMessage(type, (User) args[0]);
            else if(args.length == 2)
                return new APIMessage(type, (User) args[0], (Boolean) args[1]);
        }
        return null;
    }
}