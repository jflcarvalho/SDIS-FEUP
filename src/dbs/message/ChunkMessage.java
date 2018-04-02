package dbs.message;

import dbs.utils.Constants;

import java.nio.charset.StandardCharsets;

import static dbs.utils.Constants.CRLF_D;
import static dbs.utils.Constants.SPACE;

public class ChunkMessage extends Message {
    final int chunk_ID;
    final byte[] body;

    ChunkMessage(double version, String sender_ID, String fileID, int chunkID, byte[] data) {
        super(version, sender_ID, fileID);
        this.messageType = Constants.MessageType.CHUNK;
        this.chunk_ID = chunkID;
        this.body = data;
    }

    public int getChunkNO() {
        return chunk_ID;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public byte[] encode() {
        byte[] header = (this.messageType
                + SPACE + this.version
                + SPACE + this.sender_ID
                + SPACE + this.file_ID
                + SPACE + this.chunk_ID
                + SPACE + CRLF_D).getBytes(StandardCharsets.ISO_8859_1);
        byte[] packet = new byte[header.length + body.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(body, 0, packet, header.length, body.length);
        return packet;
    }
}
