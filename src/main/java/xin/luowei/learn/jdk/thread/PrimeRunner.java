package xin.luowei.learn.jdk.thread;

public class PrimeRunner implements Runnable {
    private final int scope = 100000;
    int n = 0;

    public PrimeRunner(int n) {
        this.n = n;
    }

    @Override
    public void run() {
        TestCreateThread.snapshoot.add(new SystemSnapshoot("" + n));
        PrimeCalculate.printPrime(scope);
        TestCreateThread.snapshoot.add(new SystemSnapshoot("" + n));
    }

   
}