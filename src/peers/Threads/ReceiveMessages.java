package peers.Threads;

import network.Network;
import peers.Protocol.Message;
import peers.ChordNode;
import peers.Protocol.MessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static utils.Utils.exceptionPrint;

public class ReceiveMessages implements Runnable {

    private ChordNode _node;
    private ServerSocket server_Socket;
    private boolean watching;
    private MessageHandler msgHandler;

    public ReceiveMessages(ChordNode node) {
        _node = node;
        msgHandler = new MessageHandler();
        try {
            this.server_Socket = new ServerSocket(node.getPort());
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Creating Server Socket");
        }
    }

    public void stop() {
        this.watching = false;
    }

    @Override
    public void run() {
        // TODO: pool of socket or digest...
        watching = true;
        while (watching){
            Socket s;
            try {
                s = server_Socket.accept();
            } catch (IOException e) {
                exceptionPrint(e, "[ERROR] Accepting connection");
                continue;
            }
            Message incomingMsg = Network.getResponse(s);
            new Thread(() -> msgHandler.handle(_node, incomingMsg, s)).start();

        }
    }
}
