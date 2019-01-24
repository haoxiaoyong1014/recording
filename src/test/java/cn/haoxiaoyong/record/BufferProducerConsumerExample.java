package cn.haoxiaoyong.record;

import cn.haoxiaoyong.record.thread.Buffer;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class BufferProducerConsumerExample {

    public static void main(String[] args) throws InterruptedException {

        final Buffer buffer = new Buffer(10);
        Thread producerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    buffer.produce();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread comsumerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    buffer.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        producerThread.start();
        comsumerThread.start();
        producerThread.join();
        producerThread.join();
    }

}
