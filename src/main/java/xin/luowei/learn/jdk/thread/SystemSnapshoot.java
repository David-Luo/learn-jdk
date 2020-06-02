package xin.luowei.learn.jdk.thread;

public class SystemSnapshoot {
    private String threadName;
    private long timestemap;
    private long memUsedSize;
    private String id;

    public SystemSnapshoot(String id){ 
        this.id = id;
        this.timestemap = System.currentTimeMillis();
        this.threadName = Thread.currentThread().getName();
        Runtime rt = Runtime.getRuntime();
        this.memUsedSize = rt.totalMemory() -rt.freeMemory();
    }

    @Override
    public String toString() {
        return "SystemSnapshoot [id=" + id + ", memUsedSize=" + memUsedSize + ", threadName=" + threadName
                + ", timestemap=" + timestemap + "]";
    }

    public String formate(String seperater) {
        return id+seperater+threadName+seperater+timestemap+seperater+memUsedSize;
    }

    public long getTimestemap() {
        return timestemap;
    }
}