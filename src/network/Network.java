package network;

import peers.Protocol.Message;
import peers.Node;

import java.io.*;
import java.net.Socket;

import static utils.Utils.exceptionPrint;
import static utils.Utils.sleepThread;

public abstract class Network {
    public static Message sendRequest(Node server, Message request, boolean reply){
        if(server == null || request == null)
            return null;

        Socket socket = send(server, request);
        if(socket == null)
            return null;

        Message reply_MSG = null;
        if(reply) {
            sleepThread(100);
            reply_MSG = getResponse(socket, server);
        }

        close(socket);
        return reply_MSG;
    }

    private static Socket send(Node server, Message request){
        Socket socket = null;

        try {
            socket = new Socket(server.getIP(), server.getPort());
            ObjectOutputStream oStream = new ObjectOutputStream(socket.getOutputStream());
            oStream.writeObject(request);
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Cannot connect to the server: " + server.getStringAddress());
        }

        return socket;
    }

    public static Message getResponse(Socket socket, Node server){
        Message reply = null;

        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            reply = (Message) in.readObject();
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Cannot read reply from: " + server.getStringAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

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
