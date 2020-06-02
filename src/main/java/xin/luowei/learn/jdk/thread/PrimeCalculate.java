package xin.luowei.learn.jdk.thread;

public class PrimeCalculate {
     /**
     * 打印自然数n以内的素数
     * 
     * @param n
     */
    public static void printPrime(int n) {
        System.out.printf("%s start to find %d\n", Thread.currentThread().getName(), n);

        // 是否为质数
        boolean isPrime;
        int prime = 1;
        for (int i = 1; i <= n; i++) {
            isPrime = true;
            for (int j = 2; j < i; j++) {
                // 若能除尽，则不为质数
                if ((i % j) == 0) {
                    isPrime = false;
                    break;
                }
            }
            // 如果是质数，则打印
            if (isPrime) {
                prime = i;
            }
        }
        System.out.printf("%s find in %d result: %d\n",Thread.currentThread().getName(), n, prime);
    }
}