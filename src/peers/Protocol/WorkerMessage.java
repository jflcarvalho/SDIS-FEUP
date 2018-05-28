package peers.Protocol;

import java.io.Serializable;

public class WorkerMessage extends Message {
    private Serializable arg;

    WorkerMessage(MessageType type, Serializable task) {
        super(type);
        this.arg = task;
    }

    public Serializable getArg() {
        return arg;
    }
}
