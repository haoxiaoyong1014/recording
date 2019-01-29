package cn.haoxiaoyong.record.thread;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * 死锁
 */
public class LockThread {

    private static String left = "筷子左";
    private static String right = "筷子右";

    public static void main(String[] args) {
        new Thread() {
            public void run() {
                while (true) {
                    synchronized (left) {
                        System.out.println(getName() + "...拿到" + left + "等待" + right);
                        synchronized (right) {
                            System.out.println(getName() + "...拿到" + right + "开吃");
                        }
                    }
                }
            }
        }.start();

        new Thread() {
            public void run() {
                while (true) {
                    synchronized (right) {
                        System.out.println(getName() + "...拿到" + right + "等待" + left);
                        synchronized (left) {
                            System.out.println(getName() + "...拿到" + left + "开吃");
                        }
                    }
                }
            }
        }.start();
    }
}
