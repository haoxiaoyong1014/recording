package cn.haoxiaoyong.record.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ProducerConsumerWithBlockingQueue {

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<Integer> blockingQueue = new LinkedBlockingDeque<Integer>(10);

        Thread producerThread = new Thread(() -> {
            int value = 0;
            while (true) {
                try {
                    blockingQueue.put(value);
                    System.out.println("生产 " + value);
                    value++;
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread consumerThread = new Thread(() -> {
            while (true) {
                try {
                    int value = blockingQueue.take();
                    System.out.println("消费 " + value);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        producerThread.start();
        consumerThread.start();
        producerThread.join();
        consumerThread.join();

    }
}
