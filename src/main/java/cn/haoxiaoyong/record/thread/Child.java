package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.Kuang;

/**
 * Created by haoxy on 2019/1/17.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class Child extends Thread {

    @Override
    public void run() {
        while (true) {
            synchronized (Kuang.kuang) {
                //1 框里没有水果就让小孩休息
                if (Kuang.kuang.size() == 0) {
                    try {
                        Kuang.kuang.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 2 小孩吃水果
                Kuang.kuang.remove("apple");
                System.out.println("小孩吃了一个水果,目前框里有" + Kuang.kuang.size() + "个水果");
                //唤醒农夫继续放水果
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
