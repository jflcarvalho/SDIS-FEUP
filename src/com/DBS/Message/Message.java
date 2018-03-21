package com.DBS.Message;

import java.util.Arrays;

import static com.Utils.Constants.CRLF_D;
import static com.Utils.Constants.SPACE;
import static com.Utils.Constants.MessageType;

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
                    MessageType.PUTCHUNK,
                    Integer.parseInt(requestSplited[1]),
                    Integer.parseInt(requestSplited[2]),
                    requestSplited[3],
                    Integer.parseInt(requestSplited[4])
            );
        } else if ("STORED".equals(requestSplited[0])){
            return new Message(
                    MessageType.STORED,
                    Integer.parseInt(requestSplited[1]),
                    Integer.parseInt(requestSplited[2]),
                    requestSplited[3],
                    Integer.parseInt(requestSplited[4]),
                    Integer.parseInt(requestSplited[5]),
                    requestSplited[6].getBytes()
            );
        }
        return null;
    }

    public static String encode(Message message){
        if(null == message)
            return null;
        String stringMessage = "";
        stringMessage += message.messageType
        + " " + message.version
        + " " + message.sender_ID
        + " " + message.file_ID
        + " " + message.chunk_NO
        + (message.replication_Deg < 0 ? "" : " " + message.replication_Deg)
        + "\n\n"
        + Arrays.toString(message.body);
        return stringMessage;
    }
}
