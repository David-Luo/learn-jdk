package xin.luowei.learn.jdk.thread.pool.sigint;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GracefulShutdownExecutors {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    /**
     * 创建支持优雅关机的线程池
     */
    public static ThreadPoolExecutor create(final ThreadPoolExecutorProperties properties) {

        final TimeUnit timeUnit = properties.getTimeUnit();
        final String groupName = properties.getPoolName();

        final ThreadFactory threadFactory = groupName == null ? Executors.defaultThreadFactory()
                : new NamedThreadFactory(groupName);
        final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(properties.getMaxQueueSize());

        
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaximumPoolSize(), properties.getKeepAliveTime(), timeUnit, workQueue, threadFactory);

        final Long awaitTerminationTime = properties.getAwaitTerminationTime();
        addShutdownHook(executor, timeUnit, awaitTerminationTime, groupName);
        return executor;
    }

    public static void addShutdownHook(final ThreadPoolExecutor executor, final TimeUnit timeUnit,
            final Long awaitTerminationTime, String poolName) {
        final WeakReference<ThreadPoolExecutor> wr = new WeakReference<>(executor);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                // System.out.printf("shutdown %s \n", poolName);
                if (wr == null || wr.get() == null) {
                    return;
                }
                final ThreadPoolExecutor pool = wr.get();
                if (pool.isShutdown()) {
                    return;
                }
                try {
                    pool.shutdown();
                    pool.awaitTermination(awaitTerminationTime, timeUnit);
                    List<Runnable> running = pool.shutdownNow();
                    System.out.printf("%s %s job has be discarded\n", poolName, running.size());
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }, poolName + "-shutdowhook-" + poolNumber.incrementAndGet()));
    }
}