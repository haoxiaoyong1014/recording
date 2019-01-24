package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.MyBlockQueue;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ComsumerQueue extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                int value = MyBlockQueue.blockingQueue.take();
                System.out.println("消费 " + value);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
