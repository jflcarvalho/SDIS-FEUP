package network;

import peers.Node;
import peers.Protocol.Message;

import java.io.*;
import java.net.Socket;

import static utils.Utils.exceptionPrint;
import static utils.Utils.sleepThread;

public abstract class Network {
    public static Message sendRequest(Node server, Message request, boolean reply){
        if(server == null || request == null)
            return null;

        Socket socket = open(server);
        if(socket == null)
            return null;
        send(socket, request);

        Message reply_MSG = null;
        if(reply) {
            sleepThread(100);
            reply_MSG = getResponse(socket);
        }

        close(socket);
        return reply_MSG;
    }

    private static Socket open(Node server){
        try {
            return new Socket(server.getIP(), server.getPort());
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Cannot connect to the server: " + server.getStringAddress());
        }
        return null;
    }

    public static boolean send(Socket socket, Message request){
        //System.out.println("SENDING_" + request.getType() + "_" + request.getNode().getStringAddress() + socket.toString());
        try {
            ObjectOutputStream oStream = new ObjectOutputStream(socket.getOutputStream());
            oStream.writeObject(request);
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Sending message ");
            return false;
        }
        return true;
    }

    public static Message getResponse(Socket socket){
        Message reply = null;

        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            reply = (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            exceptionPrint(e, "[ERROR] Cannot read reply Server");
        }

        //System.out.println("RECEIVED_" + reply.getType() + "_" + reply.getNode().getStringAddress() + socket.toString());

        return reply;
    }

    private static void close(Socket socket){
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot close socket", e);
        }
    }
}
