package xin.luowei.learn.jdk.thread;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestCreateThread {

    public static Queue<SystemSnapshoot> snapshoot = new ConcurrentLinkedQueue<>();

    public void singleThread(int start, int times) {
        snapshoot.add(new SystemSnapshoot("main"));
        for (int i = start; i > start - times; i--) {
            PrimeRunner t = new PrimeRunner(i);
            t.run();
        }
        snapshoot.add(new SystemSnapshoot("main"));
    }

    public long cost() {
        Optional<Long> min = snapshoot.stream().map(SystemSnapshoot::getTimestemap).min(Long::compare);
        Optional<Long> max = snapshoot.stream().map(SystemSnapshoot::getTimestemap).max(Long::compare);
        return max.get() - min.get();
    }

    public void useThread(int start, int times) {
        snapshoot.add(new SystemSnapshoot("main"));
        for (int i = start; i > start - times; i--) {
            Thread t = new Thread(new PrimeRunner(i));
            t.start();
        }
        snapshoot.add(new SystemSnapshoot("main"));

        try {
            Thread.sleep(678872L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        int start = 100000;// Integer.MAX_VALUE;
        int times = 2000;
        TestCreateThread t = new TestCreateThread();
        // t.useThread(start, times);
        t.singleThread(start, times);
        System.out.println("cost: " + t.cost());
    }
}