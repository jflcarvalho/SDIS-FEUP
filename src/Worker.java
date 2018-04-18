import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Worker {
    /** Worker ID */
    private String worker_id;

    /** Thread Pool to execute the Tasks, with 10 threads running concurrently */
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 10, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));

    /**
     * Constructor
     * @param worker_id - ID of Created Worker
     */
    public Worker(String worker_id) {
        this.worker_id = worker_id;
    }

    public void execute(Runnable task){
        threadPool.execute(task);
    }
}
