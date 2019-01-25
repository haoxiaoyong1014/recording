package cn.haoxiaoyong.record.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by haoxy on 2019/1/25.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class Demo3_Printer {

    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition c1 = reentrantLock.newCondition();
    private Condition c2 = reentrantLock.newCondition();
    private Condition c3 = reentrantLock.newCondition();

    private int flag = 1;

    public void print1() throws InterruptedException {
        reentrantLock.lock();      //获得锁
        if (flag != 1) {
            c1.await();            //条件满足等待
        }
        System.out.print("北");
        System.out.print("京");
        System.out.print("欢");
        System.out.print("迎");
        System.out.print("你");
        System.out.println("\r\n");
        flag = 2;
        c2.signal();                //唤醒c2
        reentrantLock.unlock();     //释放锁
    }
    public void print2() throws InterruptedException {
        reentrantLock.lock();      //获得锁
        if (flag != 2) {
            c2.await();            //条件满足等待
        }
        System.out.print("为");
        System.out.print("你");
        System.out.print("开");
        System.out.print("天");
        System.out.print("辟");
        System.out.print("地");
        System.out.println("\n");
        flag = 3;
        c3.signal();                //唤醒c3
        reentrantLock.unlock();     //释放锁
    }
    public void print3() throws InterruptedException {
        reentrantLock.lock();      //获得锁
        if (flag != 3) {
            c3.await();            //条件满足等待
        }
        System.out.print("哈");
        System.out.print("哈");
        System.out.print("骗");
        System.out.print("你");
        System.out.print("的");
        System.out.println("\n");
        flag = 1;
        c1.signal();                //唤醒c1
        reentrantLock.unlock();     //释放锁
    }
}
