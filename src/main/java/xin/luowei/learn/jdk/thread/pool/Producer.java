package xin.luowei.learn.jdk.thread.pool;

import java.util.Queue;

public class Producer implements Runnable {
    private Queue<Integer> queue;

    public Producer(Queue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println("Producer is running");
        int i=1;
        while (!Thread.currentThread().isInterrupted()) {
            queue.offer(++i);
            System.out.printf("produce %d\n", i);
            try {
                Thread.sleep(10L);//60L
            } catch (InterruptedException e) {
                // e.printStackTrace();
                System.out.printf("produce break %d, %s\n", i, Thread.currentThread().isInterrupted());
                return;
            }
        }
    }
}