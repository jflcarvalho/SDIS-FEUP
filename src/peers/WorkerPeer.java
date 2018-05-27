package peers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface WorkerPeer {
    /** Thread Pool to execute the Tasks, with 10 threads running concurrently */
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 10, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));

    boolean execute(Runnable task);
}
