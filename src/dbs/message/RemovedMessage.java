package dbs.message;

import java.nio.charset.StandardCharsets;

import static dbs.utils.Constants.CRLF_D;
import static dbs.utils.Constants.MessageType.REMOVED;
import static dbs.utils.Constants.SPACE;

public class RemovedMessage extends Message {
    private int chunk_NO;

    public RemovedMessage(double version, String sender_ID, String file_ID, int chunk_NO) {
        super(version, sender_ID, file_ID);
        messageType = REMOVED;
        this.chunk_NO = chunk_NO;
    }

    public int getChunkID() {
        return chunk_NO;
    }

    @Override
    public byte[] encode() {
        return (this.messageType
                + SPACE + this.version
                + SPACE + this.sender_ID
                + SPACE + this.file_ID
                + SPACE + this.chunk_NO
                + SPACE + CRLF_D).getBytes(StandardCharsets.ISO_8859_1);
    }
}
