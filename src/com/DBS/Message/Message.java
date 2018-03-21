package com.DBS.Message;

import static com.Utils.Constants.*;
import static com.Utils.Constants.MessageType.PUTCHUNK;

public class Message {

    private MessageType messageType;
    private int version;
    private int sender_ID;
    private String file_ID;
    private int chunk_NO;
    private int replication_Deg;
    private byte[] body;

    public Message(MessageType messageType, int version, int sender_ID, String file_ID, int chunk_NO, int replication_Deg, byte[] body) {
        this.messageType = messageType;
        this.version = version;
        this.sender_ID = sender_ID;
        this.file_ID = file_ID;
        this.chunk_NO = chunk_NO;
        this.replication_Deg = replication_Deg;
        this.body = body;
    }

    public Message(MessageType messageType, int version, int sender_ID, String file_ID, int chunk_NO) {
        this.messageType = messageType;
        this.version = version;
        this.sender_ID = sender_ID;
        this.file_ID = file_ID;
        this.chunk_NO = chunk_NO;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public int getVersion() {
        return version;
    }

    public int getSender_ID() {
        return sender_ID;
    }

    public String getFile_ID() {
        return file_ID;
    }

    public int getChunk_NO() {
        return chunk_NO;
    }

    public int getReplication_Deg() {
        return replication_Deg;
    }

    public byte[] getBody() {
        return body;
    }

    public static Message parse(String request){
        if(null == request)
            return null;
        String[] requestSplited = request.split( SPACE+"(?=.+"+ CRLF_D +")| "+CRLF_D);
        if("PUTCHUNK".equals(requestSplited[0])){
            return new Message(
                    PUTCHUNK,
                    Integer.parseInt(requestSplited[1]),
                    Integer.parseInt(requestSplited[2]),
                    requestSplited[3],
                    Integer.parseInt(requestSplited[4]),
                    Integer.parseInt(requestSplited[5]),
                    requestSplited[6].getBytes()
            );
        } else if ("STORED".equals(requestSplited[0])){
            return new Message(
                    MessageType.STORED,
                    Integer.parseInt(requestSplited[1]),
                    Integer.parseInt(requestSplited[2]),
                    requestSplited[3],
                    Integer.parseInt(requestSplited[4])
            );
        }
        return null;
    }

    public static String encode(Message message){
        if(null == message)
            return null;
        String stringMessage = "";
        switch (message.messageType) {
            case PUTCHUNK:
                stringMessage += message.messageType
                    + SPACE + message.version
                    + SPACE + message.sender_ID
                    + SPACE + message.file_ID
                    + SPACE + message.chunk_NO
                    + (message.replication_Deg < 0 ? "" : SPACE + message.replication_Deg)
                    + CRLF_D
                    + new String(message.body);
                break;
            case STORED:
                stringMessage += message.messageType
                        + SPACE + message.version
                        + SPACE + message.sender_ID
                        + SPACE + message.file_ID
                        + SPACE + message.chunk_NO
                        + CRLF_D;
                break;
        }
        return stringMessage;
    }
}
