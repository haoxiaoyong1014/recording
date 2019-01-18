package cn.haoxiaoyong.record.thread2;

/**
 * Created by haoxy on 2019/1/17.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class OneAndTwo {

    static volatile int i = 0, j = 0;

    public /*synchronized*/ static void one() {
        i++;
        j++;
    }

    public /*synchronized*/ static void two() {
        System.out.println("i=" + i + " j=" + j);
    }
}
