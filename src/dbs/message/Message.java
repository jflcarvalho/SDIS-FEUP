package dbs.message;

import dbs.utils.Constants;

import java.nio.charset.StandardCharsets;

import static dbs.utils.Constants.*;

public abstract class Message {
    protected Constants.MessageType messageType;
    protected double version;
    protected String sender_ID;
    protected String file_ID;

    public Message(double version, String sender_ID, String file_ID) {
        this.version = version;
        this.sender_ID = sender_ID;
        this.file_ID = file_ID;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public double getVersion() {
        return version;
    }

    public String getSenderID() {
        return sender_ID;
    }

    public String getFileID() {
        return file_ID;
    }

    public static Message parse(String request){
        String[] requestSpliced = request.split( SPACE+"(?=.+"+ CRLF_D +")| "+CRLF_D);
        switch (requestSpliced[0]){
            case "PUTCHUNK":
                return new PutChunkMessage(
                        Double.parseDouble(requestSpliced[1]),
                        requestSpliced[2],
                        requestSpliced[3],
                        Integer.parseInt(requestSpliced[4]),
                        Integer.parseInt(requestSpliced[5]),
                        requestSpliced[6].getBytes(StandardCharsets.ISO_8859_1)
                );
            case "STORED":
                return new StoredMessage(
                        Double.parseDouble(requestSpliced[1]),
                        requestSpliced[2],
                        requestSpliced[3],
                        Integer.parseInt(requestSpliced[4])
                );
            case "DELETE":
                return new DeleteMessage(
                        Double.parseDouble(requestSpliced[1]),
                        requestSpliced[2],
                        requestSpliced[3]
                );
            case "CHUNK":
                return new ChunkMessage(
                        Double.parseDouble(requestSpliced[1]),
                        requestSpliced[2],
                        requestSpliced[3],
                        Integer.parseInt(requestSpliced[4]),
                        requestSpliced[5].getBytes(StandardCharsets.ISO_8859_1)
                );
            case "GETCHUNK":
                return new GetChunkMessage(
                        Double.parseDouble(requestSpliced[1]),
                        requestSpliced[2],
                        requestSpliced[3],
                        Integer.parseInt(requestSpliced[4])
                );
            case "REMOVED":
                return new RemovedMessage(
                        Double.parseDouble(requestSpliced[1]),
                        requestSpliced[2],
                        requestSpliced[3],
                        Integer.parseInt(requestSpliced[4])
                );
            default:
                break;
        }
        return null;
    }

    public abstract byte[] encode();
}
