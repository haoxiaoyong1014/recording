package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.thread.ComsumerQueue;
import cn.haoxiaoyong.record.thread.ProducerQueue;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ProducerComsumerQueueTest {

    public static void main(String[] args) throws InterruptedException {
        ProducerQueue producerQueue = new ProducerQueue();
        ComsumerQueue comsumerQueue = new ComsumerQueue();
        producerQueue.start();
        comsumerQueue.start();
        producerQueue.join();
        comsumerQueue.join();
    }
}
