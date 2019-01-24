package cn.haoxiaoyong.record;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ProtectThread {

    public static void main(String[] args) {

        Thread t1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    System.out.println(getName() + " ...aaaaaaaa");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(getName()+ " ...bb");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.setDaemon(true);             //将t1设置为守护线程
        t1.start();
        t2.start();
    }
}
