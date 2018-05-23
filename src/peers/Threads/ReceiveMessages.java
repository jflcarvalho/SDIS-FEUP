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

    private ChordNode _node = null;
    private ServerSocket server_Socket;
    private boolean watching;
    private MessageHandler msgHandler;

    public ReceiveMessages(ChordNode node, int port) {
        _node = node;
        msgHandler = new MessageHandler();
        try {
            this.server_Socket = new ServerSocket(port);
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Creating Server Socket");
        }
    }

    public void stop() {
        this.watching = false;
    }

    @Override
    public void run() {
        Socket s;
        watching = true;
        while (watching){
            try {
                s = server_Socket.accept();
            } catch (IOException e) {
                exceptionPrint(e, "[ERROR] Accepting connection");
                continue;
            }
            Message incommingMsg = Network.getResponse(s, _node);
            msgHandler.handle(_node, incommingMsg);
        }
    }
}
