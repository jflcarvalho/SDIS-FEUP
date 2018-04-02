package dbs.message;

import dbs.utils.Constants;

import java.nio.charset.StandardCharsets;

import static dbs.utils.Constants.CRLF_D;
import static dbs.utils.Constants.SPACE;

public class PutChunkMessage extends ChunkMessage {
    private final int replication_Deg;

    PutChunkMessage(double version, String sender_ID, String file_ID, int chunk_NO, int replication_Deg,  byte[] body) {
        super(version, sender_ID, file_ID, chunk_NO, body);
        this.messageType = Constants.MessageType.PUTCHUNK;
        this.replication_Deg = replication_Deg;
    }

    public int getReplicationDeg() {
        return replication_Deg;
    }

    @Override
    public byte[] encode() {
        byte[] header = (this.messageType
                + SPACE + this.version
                + SPACE + this.sender_ID
                + SPACE + this.file_ID
                + SPACE + this.chunk_ID
                + (this.replication_Deg < 0 ? "" : SPACE + this.replication_Deg)
                + SPACE + CRLF_D).getBytes(StandardCharsets.ISO_8859_1);
        byte[] packet = new byte[header.length + body.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(body, 0, packet, header.length, body.length);
        return packet;
    }
}
