package xin.luowei.learn.jdk;

import java.util.LinkedList;
import java.util.List;

/**
* 多线程
 * ThreadJoin
 */
public class ThreadJoin {

    public static void main(final String[] args) {
        System.out.println("boss:  hi guys, go to work");
        final List<LazyBoy> list = new LinkedList<>();
        list.add(new LazyBoy("John"));
        list.add(new LazyBoy("ella"));

        for (final LazyBoy lazyBoy : list) {
            lazyBoy.start();
        }
        System.out.println("boss:  hurry ");
        for (final LazyBoy lazyBoy : list) {
            try {
                lazyBoy.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("boss:  oh my god ");
    }

}

class LazyBoy extends Thread {
    String name;

    public LazyBoy(final String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(name + ":  I don`t do anything.");
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name + ":  go back home!");
    }
}