package cn.haoxiaoyong.record.thread2;

/**
 * Created by haoxy on 2019/1/17.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class TestOneAndTwo {

    /**
     * volatile可以保证内存可见性，不能保证并发有序性
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

}
