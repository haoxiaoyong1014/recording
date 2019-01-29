package cn.haoxiaoyong.record.thread;

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
        System.out.println(ThreadFor1.currentThread());//获取线程对象
        ThreadFor2 for2=new ThreadFor2();
        for2.setName("线程B");
        for1.start();
        for2.start();
        Thread.currentThread().setName("我是主线程");//获取主函数线程的引用,并改名字
        System.out.println(Thread.currentThread().getName());
    }
}
