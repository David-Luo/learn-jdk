package xin.luowei.learn.jdk.thread.pool.sigint;

import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorProperties {

    private int corePoolSize = 2;
    private int maximumPoolSize = 2;
    private int maxQueueSize = 1000000;
    private long keepAliveTime = 200;
    private long awaitTerminationTime = 30;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private String poolName = null;

    // 

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getAwaitTerminationTime() {
        return awaitTerminationTime;
    }

    public void setAwaitTerminationTime(long awaitTerminationTime) {
        this.awaitTerminationTime = awaitTerminationTime;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

}