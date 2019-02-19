package cn.haoxiaoyong.record.thread2;

/**
 * Created by haoxy on 2019/1/17.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * 这是一道面试题
 *  题目：子线程循环10次，主线程循环100次，如此循环100次
 */
public class Business {

    private boolean bool = true;

    public synchronized void main(int loop) throws InterruptedException {
        while (bool) {
            this.wait();
        }
        for (int i = 0; i < 100; i++) {
            System.out.println("Main thread seq of " + i + ", loop of " + loop);
        }
        bool = true;
        this.notify();
    }

    public synchronized void sub(int loop) throws InterruptedException {
        while (!bool) {
            this.wait();
        }
        for (int i = 0; i < 10; i++) {
            System.out.println("sub thread seq of " + i + ", loop of " + loop);
        }
        bool = false;
        this.notify();
    }
}
