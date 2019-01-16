package cn.haoxiaoyong.record.thread;

/**
 * Created by haoxy on 2019/1/16.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ThreadFor2 extends Thread {

    public void run() {
        for (int i = 51; i < 100; i++) {
            System.out.println(this.getName() + ":" + i);
        }
    }
}
