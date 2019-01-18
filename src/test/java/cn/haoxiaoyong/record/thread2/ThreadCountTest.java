package cn.haoxiaoyong.record.thread2;

import java.util.*;

/**
 * Created by haoxy on 2019/1/17.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ThreadCountTest {

    public static void main(String[] args) {
        final Count count = new Count();
        Runnable runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    count.increment();
                }

            }
        };
        List<Thread> threads = new ArrayList<Thread>(10);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(runnable);
            threads.add(thread);
            thread.start();
        }
        while (true) {
            if (allThreadTerminated(threads)){//所有线程运行结束
                System.out.println(count.get());
                break;
            }
        }
    }

    private static boolean allThreadTerminated(List<Thread> threads) {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                return false;
            }
        }
        return true;
    }
}
