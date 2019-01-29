package cn.haoxiaoyong.record.thread;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * 加入线程
 */
public class JoinTread {

    public static void main(String[] args) {
        final Thread t1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    System.out.println(getName() + " ....aaaaa");
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
                for (int i = 0; i < 1000; i++) {
                    if (i == 2) {
                        try {
                            //t1.join();              //插队,加入
                            t1.join(30);
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(getName() + " ....bbb");
                }
            }
        };
        t1.start();
        t2.start();
    }
}
