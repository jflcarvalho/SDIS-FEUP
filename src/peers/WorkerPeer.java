package peers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface WorkerPeer {
    int CORE_SIZE = 1;
    int MAX_THREADS = 2;
    int TIME_LIMIT = 60;
    int MAX_IN_QUEUE = 50;
    /** Thread Pool to execute the Tasks, with 10 threads running concurrently */
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_SIZE, MAX_THREADS, TIME_LIMIT, TimeUnit.SECONDS, new ArrayBlockingQueue<>(MAX_IN_QUEUE));

    /** Runnables in queue */
    ConcurrentHashMap<Integer, Runnable> runnable_Queue = new ConcurrentHashMap<>();

    boolean execute(Task task);

    void deletePendingTask(Task task);
}
