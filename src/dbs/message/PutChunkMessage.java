package dbs.message;

import dbs.utils.Constants;

import static dbs.utils.Constants.CRLF_D;
import static dbs.utils.Constants.SPACE;

public class PutChunkMessage extends Message {
    private int chunk_NO;
    private int replication_Deg;
    private byte[] body;

    PutChunkMessage(double version, String sender_ID, String file_ID) {
        super(Constants.MessageType.PUTCHUNK, version, sender_ID, file_ID);
        this.chunk_NO = chunk_NO;
        this.replication_Deg = replication_Deg;
        this.body = body;
    }

    void addChunkInfo(int chunk_NO, int replication_Deg,  byte[] body){
        this.chunk_NO = chunk_NO;
        this.replication_Deg = replication_Deg;
        this.body = body;
    }

    public int getChunkNO() {
        return chunk_NO;
    }

    public int getReplicationDeg() {
        return replication_Deg;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String encode() {
        return this.messageType
                + SPACE + this.version
                + SPACE + this.sender_ID
                + SPACE + this.file_ID
                + SPACE + this.chunk_NO
                + (this.replication_Deg < 0 ? "" : SPACE + this.replication_Deg)
                + SPACE + CRLF_D;
    }
}
