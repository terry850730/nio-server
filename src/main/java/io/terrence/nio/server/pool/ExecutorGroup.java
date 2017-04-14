package io.terrence.nio.server.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Terrence on 2017/4/13.
 * Desc:
 */
public class ExecutorGroup {

    private static Logger logger = LoggerFactory.getLogger(ExecutorGroup.class);

    private int corePoolSize = 10;
    private int maximumPoolSize = 100;
    private long keepAliveTime = 60;
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(500);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}
