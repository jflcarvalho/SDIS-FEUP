package peers.Threads;

import network.Network;
import peers.*;
import peers.Protocol.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static utils.Utils.exceptionPrint;

public class ReceiveMessages implements Runnable {

    private ChordNode _node;
    private ServerSocket server_Socket;
    private boolean watching;
    private MessageHandler msgHandler;
    private ScheduledThreadPoolExecutor executor;
    private final int MAX_THREADS = 10;

    public ReceiveMessages(ChordNode node) {
        _node = node;
        executor = new ScheduledThreadPoolExecutor(MAX_THREADS);
        msgHandler = new MessageHandler();
        try {
            this.server_Socket = new ServerSocket(node.getPort());
        } catch (IOException e) {
            exceptionPrint(e, "[ERROR] Creating Server Socket");
            System.exit(-1);
        }
    }

    public void stop() {
        this.watching = false;
    }

    @Override
    public void run() {
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
            if(incomingMsg instanceof DatabaseMessage && _node instanceof DatabaseManager)
                executor.execute(() -> msgHandler.handle(((DatabaseManager) _node), incomingMsg, s));
            else if(!(incomingMsg instanceof APIMessage))
                executor.execute(() -> msgHandler.handle(_node, incomingMsg, s));
            else if(_node instanceof DatabaseManager)
                executor.execute(() -> msgHandler.handle(((DatabaseManager) _node), incomingMsg, s));
        }
    }
}
