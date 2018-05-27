package peers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Worker extends ChordNode implements WorkerPeer{
    public Worker(int node_Port) {
        super(node_Port);
    }

    @Override
    public boolean execute(Runnable task){
        threadPool.execute(task);
        return true;
    }
}
