package cn.haoxiaoyong.record;

import cn.haoxiaoyong.record.thread.ThreadFor1;
import cn.haoxiaoyong.record.thread.ThreadFor2;
import org.junit.Test;

/**
 * Created by haoxy on 2019/1/16.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class TestThreadFor {

    @Test
    public void testThread() {
        ThreadFor1 for1 = new ThreadFor1();
        for1.setName("线程A");
        ThreadFor2 for2=new ThreadFor2();
        for2.setName("线程B");
        for1.start();
        for2.start();
    }
}
