package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.MyBlockQueue;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ProducerQueue extends Thread{

    @Override
    public void run() {
        int value = 0;
        while (true) {
            try {
                MyBlockQueue.blockingQueue.put(value);
                System.out.println("生产 " + value);
                value++;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
