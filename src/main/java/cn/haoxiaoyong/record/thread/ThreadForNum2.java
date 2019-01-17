package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.MyLock;

/**
 * Created by haoxy on 2019/1/16.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ThreadForNum2 extends Thread {

    public void run() {
        for (int i = 0; i < 11; i++) {
            synchronized (MyLock.o) {
                System.out.println("2");
                MyLock.o.notify();//唤醒另一个线程
                try {
                    MyLock.o.wait();//让自己休眠并释放锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
