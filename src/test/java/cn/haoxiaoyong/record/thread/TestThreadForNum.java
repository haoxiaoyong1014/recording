package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.thread.ThreadForNum1;
import cn.haoxiaoyong.record.thread.ThreadForNum2;

/**
 * Created by haoxy on 2019/1/16.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class TestThreadForNum {

    public static void main(String[] args) {
        new ThreadForNum1().start();
        new ThreadForNum2().start();
    }
}
