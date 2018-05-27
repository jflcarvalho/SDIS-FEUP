package peers.Protocol;

import peers.Node;
import user.User;

public class APIMessage extends Message {
    private User user;
    private boolean replyValue;

    APIMessage(MessageType type, User user) {
        super(type);
        this.user = user;
    }

    APIMessage(MessageType type, User user, boolean replyValue) {
        super(type);
        this.user = user;
        this.replyValue = replyValue;
    }

    public User getUser() {
        return user;
    }

    public boolean getReplyValue() {
        return replyValue;
    }
}
