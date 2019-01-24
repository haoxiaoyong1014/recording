package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.Kuang;

/**
 * Created by haoxy on 2019/1/17.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class Farmer extends Thread {

    @Override
    public void run() {
        while (true) {
            synchronized (Kuang.kuang) {
                //筐放满了就让农夫休息
                if (Kuang.kuang.size() == 10) {
                    try {
                        Kuang.kuang.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //2 往筐里放水果
                Kuang.kuang.add("apple");
                System.out.println("农夫放了一个水果,目前框里有" + Kuang.kuang.size() + "个水果");
                //3唤醒小孩继续吃
                Kuang.kuang.notify();
            }
            //模拟控制速度
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
