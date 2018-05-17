package network;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import static utils.Utils.exceptionPrint;

public abstract class Network {
    public static void sendRequest(InetSocketAddress server, String request){
        if(server == null || request == null)
            return;

        // try to connect to server to send the request
        Socket socket = null;
        try {
            socket = new Socket(server.getAddress(), server.getPort());
            PrintStream oStream = new PrintStream(socket.getOutputStream());
            oStream.println(request);
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Cannot connect to the server: " +
                    server.getAddress().getHostAddress() + ":" + server.getPort());
            return;
        }

        // try to close socket
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot close socket", e);
        }
    }
}
