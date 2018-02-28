package com.Message;

public class Message {
    public enum Type {PUTCHUNK, STORED}

    private Type type;
    //private String version;
    private int sender_ID;
    private String file_ID;
    private int chunk_ID;
    private int replication_Deg;
    private char[] body;

    public static Message parse(String request){

        return null;
    }

    public static String encode(Message message){

        return null;
    }
}
