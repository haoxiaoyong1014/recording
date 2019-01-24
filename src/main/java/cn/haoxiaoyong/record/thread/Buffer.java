package cn.haoxiaoyong.record.thread;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class Buffer {

    private Queue<Integer> list;

    private int size;

    public Buffer(int size) {
        this.list = new LinkedList<Integer>();
        this.size = size;
    }

    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (this) {
                while (list.size() >= size) {
                    //等待消费者
                    System.out.println("等待消费者");
                    wait();
                }
                list.add(value);
                System.out.println("生产 " + value);
                value++;
                //通知消费者
                notify();
                //Thread.sleep(1000);
            }
            Thread.sleep(1000);//放在同步方法中和放在同步方法外效果不一样
        }
    }

    public void consume() throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (list.size() == 0) {
                    //等待生产者
                    System.out.println("等待生产者");
                    wait();
                }
                int value = list.poll();
                System.out.println("消费 " + value);
                //通知生产者
                notify();
            }
            Thread.sleep(1000);
        }
    }
}
