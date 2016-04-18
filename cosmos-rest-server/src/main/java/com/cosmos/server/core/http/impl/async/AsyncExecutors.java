package com.cosmos.server.core.http.impl.async;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Async Executors.
 *
 * @author BSD
 */
public class AsyncExecutors {

    // max async executor's queue length
    private static final int MAX_ASYNC_QUEUE_SIZE = 500000;

    // internal queue based on Array
    private static final BlockingQueue<Runnable> innerQueue = new ArrayBlockingQueue<>(MAX_ASYNC_QUEUE_SIZE);

    /**
     * thread factory for logic processing
     */
    public static final ThreadFactory factory = new ThreadFactory() {
        private final AtomicInteger poolNumber = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            // give new name
            Thread newThread = new Thread(r, String.format("netty-executor-pool-%d", poolNumber.getAndIncrement()));
            if (newThread.isDaemon())
                newThread.setDaemon(false);
            if (newThread.getPriority() != Thread.NORM_PRIORITY)
                newThread.setPriority(Thread.NORM_PRIORITY);
            return newThread;
        }
    };

    /**
     * Creates a new {@link ExecutorService} with the given parameters.
     *
     * @param workers worker number
     * @return Executor Service
     */
    public static ExecutorService newExecutors(int workers) {
        // customized executor
        return new ThreadPoolExecutor(Math.max(4, workers / 4), Math.max(4, workers),  // from total/4 to total
                10, TimeUnit.SECONDS,                                                  // 10s to recycle idle thread
                innerQueue,                                                            // bounded queue
                factory,                                                               // named factory
                new ThreadPoolExecutor.CallerRunsPolicy()                              // caller run
        );
    }
}
