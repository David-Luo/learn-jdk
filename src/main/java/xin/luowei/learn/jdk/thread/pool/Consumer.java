package xin.luowei.learn.jdk.thread.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import xin.luowei.learn.jdk.thread.PrimeCalculate;

public class Consumer implements Runnable {
    private ExecutorService executor;
    private BlockingQueue<Integer> workQueue;

    public Consumer(ExecutorService executor, BlockingQueue<Integer> workQueue) {
        this.executor = executor;
        this.workQueue = workQueue;
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("work");
            //等待数据
            if (workQueue.isEmpty()) {
                try {
                    System.out.println("waiting...");
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //在抛出InterruptedException时,Thread.currentThread().isInterrupted()已经被重置为false
                    System.out.printf("consumer break %s\n",Thread.currentThread().isInterrupted());
                    //所以catch中要么直接返回,要么重新中断
                    // Thread.currentThread().interrupt();
                    return;
                }
                continue;
            }
            //线程池关闭不再接收任务
           if (executor.isShutdown()) {
                System.out.println("pool is shutdown, could not accpet anything. ");
                return;
            } 
            Integer poll = workQueue.poll();
            System.out.println("get " + poll);
            try {
                executor.execute(() -> PrimeCalculate.printPrime(poll * 1000));
            } catch (RejectedExecutionException e) {
                System.out.println("discard "+poll);
            } 
        }
        System.out.println("currentThread is Interrupted");
    }

}