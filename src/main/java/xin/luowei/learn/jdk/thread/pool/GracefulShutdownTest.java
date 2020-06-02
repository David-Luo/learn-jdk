package xin.luowei.learn.jdk.thread.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import xin.luowei.learn.jdk.thread.pool.sigint.GracefulShutdownExecutors;
import xin.luowei.learn.jdk.thread.pool.sigint.ThreadPoolExecutorProperties;

public class GracefulShutdownTest {

    public static void gracefulShutdown() {
        BlockingQueue<Integer> workQueue = new LinkedBlockingQueue<>();

        ThreadPoolExecutorProperties producerProperties= new ThreadPoolExecutorProperties();
        producerProperties.setAwaitTerminationTime(1L);
        producerProperties.setTimeUnit(TimeUnit.MILLISECONDS);
        producerProperties.setCorePoolSize(1);
        producerProperties.setMaximumPoolSize(1);
        producerProperties.setPoolName("producer");
        ExecutorService producerExcutor = GracefulShutdownExecutors.create(producerProperties);
        producerExcutor.submit(new Producer(workQueue)); 


        ThreadPoolExecutorProperties properties= new ThreadPoolExecutorProperties();
        properties.setPoolName("workpool");
        ExecutorService executor = GracefulShutdownExecutors.create(properties);
        Consumer c = new Consumer(executor, workQueue);
        c.run();
    }

    public static void simplelyShutdown() {
        BlockingQueue<Integer> workQueue = new LinkedBlockingQueue<>();
        Producer producer = new Producer(workQueue);
        Thread producerThread = new Thread(producer,"producer");
        producerThread.start();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Consumer c = new Consumer(executor,workQueue);
        c.run();
    }

    public static void main(String[] args) {
        // simplelyShutdown();
        gracefulShutdown() ;
    }
}