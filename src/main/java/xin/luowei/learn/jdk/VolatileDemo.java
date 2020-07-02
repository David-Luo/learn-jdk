package xin.luowei.learn.jdk;

public class VolatileDemo {

    /*volatile*/ int number;

    protected void increase() {
        try {
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        number++;
    }

    public int getNumber() {
        return number;
    }

    public static void main(String[] args) {
        final VolatileDemo volDemo = new VolatileDemo();
        for (int i = 0; i < 500; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    volDemo.increase();
                }
            }).start();
        }

        // 如果还有子线程在运行,主线程就让出CPU资源,
        // 直到所有的子线程都运行完了,主线程再继续往下执行
        while (Thread.activeCount() > 1) {
            Thread.yield();
        }

        System.out.println("number: " + volDemo.getNumber());
    }

}