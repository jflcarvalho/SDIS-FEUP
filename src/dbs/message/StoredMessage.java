package dbs.message;

import dbs.utils.Constants;

import static dbs.utils.Constants.CRLF_D;
import static dbs.utils.Constants.SPACE;

public class StoredMessage extends Message{
    private int chunk_NO;

    StoredMessage(double version, String sender_ID, String file_ID, int chunkNo) {
        super(Constants.MessageType.STORED, version, sender_ID, file_ID);
        this.chunk_NO = chunkNo;
    }

    public int getChunkNO() {
        return chunk_NO;
    }

    @Override
    public String encode() {
         return this.messageType
                + SPACE + this.version
                + SPACE + this.sender_ID
                + SPACE + this.file_ID
                + SPACE + this.chunk_NO
                + SPACE + CRLF_D;
    }
}
