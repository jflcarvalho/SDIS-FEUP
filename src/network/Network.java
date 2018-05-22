package network;

import com.sun.org.apache.xpath.internal.functions.Function;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

import static utils.Utils.exceptionPrint;

public abstract class Network {
    public static void sendRequest(InetSocketAddress server, String request){
        if(server == null || request == null)
            return;

        Socket socket = send(server, request);

        // try to close socket
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot close socket", e);
        }
    }

    public static void sendRequest(InetSocketAddress server, String request, Callable<> handler){
        if(server == null || request == null || handler == null)
            return;

        Socket socket = send(server, request);

        String reply = null;
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            reply = in.readUTF();
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Cannot read reply from: "+
                    server.getAddress().getHostAddress() + ":" + server.getPort());
        }

        // try to close socket
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot close socket", e);
        }

        handler(reply);
    }

    private static Socket send(InetSocketAddress server, String request){
        Socket socket = null;
        try {
            socket = new Socket(server.getAddress(), server.getPort());
            PrintStream oStream = new PrintStream(socket.getOutputStream());
            oStream.println(request);
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Cannot connect to the server: " +
                    server.getAddress().getHostAddress() + ":" + server.getPort());
            return null;
        }
        return socket;
    }
}
