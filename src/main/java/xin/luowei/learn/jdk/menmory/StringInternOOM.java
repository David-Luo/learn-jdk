package xin.luowei.learn.jdk.menmory;

import java.util.ArrayList;
import java.util.List;

/**
 * 在虚拟机中,有一块称为常量池的区域专门用于存放字符串常量.
 * 在JDK1.6之前,常量池属于永久区的一部分,但在JDK1.7以后,它就被移到了堆中进行管理.
 */
public class StringInternOOM {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        int i=0;
        while (true) {
            list.add(String.valueOf(i++).intern());
            //使用String.intern()获得常量池中的字符串引用,将该引用放入list持有,确保不被回收.
        }
    }
    /**
     * 使用 -XX:MaxPermSize=5m限制堆大小
     * 
     */
}