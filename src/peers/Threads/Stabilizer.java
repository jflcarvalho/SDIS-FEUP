package peers.Threads;

import network.Network;
import peers.ChordNode;
import peers.Node;
import peers.Protocol.Message;
import peers.Protocol.MessageFactory;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static peers.Protocol.Message.MessageType.REPLY_GETPREDECCESSOR;

public class Stabilizer implements Runnable{
    private ChordNode _node;
    private ScheduledExecutorService executor;
    private int startDelay = 5;
    private int delayBetween = 30;

    public Stabilizer(ChordNode node) {
        _node = node;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void setStartDelay(int startDelay) {
        this.startDelay = startDelay;
    }

    public void setDelayBetween(int delayBetween) {
        this.delayBetween = delayBetween;
    }

    public void stop() {
        executor.shutdown();
    }

    @Override
    public void run() {
        executor.schedule(this::fix_fingers, startDelay, SECONDS);
        executor.scheduleWithFixedDelay(this::stabilize, (long)0.5, delayBetween, SECONDS);
    }

    private void updateDelay(int i) {
        if(i != 33 && startDelay <= 256) {
            System.out.println("FIXED");
            startDelay /= 2;
        } else if(i == 33 && startDelay >= 1){
            startDelay *= 2;
        }
    }

    private void stabilize(){
        System.out.println("Stabilizing Network...");
        Node successor = _node.getSuccessor();

        Message reply = Network.sendRequest(successor, MessageFactory.GetPredeccessor(successor), true);
        if(reply == null || reply.getType() != REPLY_GETPREDECCESSOR)
            return;
        Node x = reply.getNode();

        if(Integer.compareUnsigned(_node.difference(x), _node.difference(successor)) < 0)
            _node.updateFingerTable(1, x);

        _node.setPredecessor(_node.notify(x));

    }

    private void fix_fingers(){
        System.out.println("Fixing FingerTable...");
        int random_index = (int) (Math.random() * 31 + 1);
        Node idealNode = new Node(_node.getNode_ID() + (int) Math.pow(2, random_index - 1), null);
        Node x = _node.findSuccessor(idealNode);
        int change = _node.updateFingerTable(random_index, x);
        //updateDelay(change);
        executor.schedule(this::fix_fingers, startDelay, SECONDS);
        System.out.print(_node.fingerToString());
    }
}
