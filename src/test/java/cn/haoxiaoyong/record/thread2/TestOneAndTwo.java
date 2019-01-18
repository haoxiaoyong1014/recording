package cn.haoxiaoyong.record.thread2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by haoxy on 2019/1/17.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class TestOneAndTwo {

    /**
     * volatile可以保证内存可见性，不能保证并发有序性
     *
     * @param args
     */
    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    OneAndTwo.one();
                    OneAndTwo.two();
                }
            };
            thread.start();
        }
    }

    /**
     * 线程池
     * @param args
     */
    /*public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 1; i < 5; i++) {
            final int taskID = i;
            threadPool.execute(new Runnable() {
                public void run() {
                    for (int j = 1; j < 5; j++) {
                        try {
                            Thread.sleep(200);// 为了测试出效果，让每次任务执行都需要一定时间
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("第" + taskID + "次任务的第" + j + "次执行");
                    }
                }
            });
        }
        threadPool.shutdown();// 任务执行完毕，关闭线程池
    }*/
    /**
     * 打印结果
     * 第2次任务的第1次执行
       第3次任务的第1次执行
       第1次任务的第1次执行
       第4次任务的第1次执行
       第2次任务的第2次执行
       第3次任务的第2次执行
       第1次任务的第2次执行
       第4次任务的第2次执行
       第3次任务的第3次执行
       第4次任务的第3次执行
       第1次任务的第3次执行
       第2次任务的第3次执行
       第3次任务的第4次执行
       第4次任务的第4次执行
       第1次任务的第4次执行
       第2次任务的第4次执行
     */
}
