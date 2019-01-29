package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.thread.Child;
import cn.haoxiaoyong.record.thread.Farmer;

/**
 * Created by haoxy on 2019/1/17.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class TestFarmerChild {

    public static void main(String[] args) {
        new Farmer().start();
        new Child().start();
    }
}
