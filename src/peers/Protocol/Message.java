package peers.Protocol;

import java.io.Serializable;

public class Message implements Serializable {
    public enum MessageType {
        FIND_SUCCESSOR(0), SET_PREDECESSOR(1), GET_SUCCESSOR(2), GET_CLOSEST(3), GET_PREDECESSOR(4), UPDATE_FINGER(5),
        REPLY_FIND_SUCCESSOR(6), REPLY_SET_PREDECESSOR(7), REPLY_GET_SUCCESSOR(8), REPLY_GET_CLOSEST(9), REPLY_GET_PREDECESSOR(10),
        LOGIN(11), REPLY_LOGIN(12), REGISTER(13), REPLY_REGISTER(14), GET_LOGIN_DATA(15), REPLY_LOGIN_DATA(16), GET_TASKS_DATA(17), REPLY_TASKS_DATA(18),
        GET_TASKS(19), SAVE_TASK(20), SUBMIT(21), EXECUTE(22), DELETE_TASK(23), REPLY_GET_TASKS(24);

        private int messageId;

        MessageType(int messageId){
            this.messageId= messageId;
        }

        public int getValue(){
            return this.messageId;
        }
    }

    protected MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }
}
