package cn.haoxiaoyong.record;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class MyBlockQueue {

   public static BlockingQueue<Integer> blockingQueue = new LinkedBlockingDeque<Integer>(2);
}
