package peers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Worker extends Node{
    /** Thread Pool to execute the Tasks, with 10 threads running concurrently */
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 10, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));

    public Worker(String node_IP, int node_Port) {
        super(node_IP, node_Port);
    }

    public void execute(Runnable task){
        threadPool.execute(task);
    }
}
