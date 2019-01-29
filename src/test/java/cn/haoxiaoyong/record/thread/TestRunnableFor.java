package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.thread.RunnableFor1;
import cn.haoxiaoyong.record.thread.RunnableFor2;
import org.junit.Test;

/**
 * Created by haoxy on 2019/1/16.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class TestRunnableFor {

    @Test
    public void testRunnable() {
        Thread t1 = new Thread(new RunnableFor1());
        t1.setName("线程A");
        Thread t2 = new Thread(new RunnableFor2());
        t2.setName("线程B");
        t1.start();
        t2.start();
    }
}
