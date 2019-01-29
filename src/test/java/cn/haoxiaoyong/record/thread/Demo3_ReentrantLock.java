package cn.haoxiaoyong.record.thread;

import cn.haoxiaoyong.record.thread.Demo3_Printer;

/**
 * Created by haoxy on 2019/1/25.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class Demo3_ReentrantLock {

    public static void main(String[] args) {
       final Demo3_Printer demo3_printer=new Demo3_Printer();
        new Thread(){
            public void run(){
                while (true){
                    try {
                        demo3_printer.print1();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        new Thread(){
            public void run(){
                while (true){
                    try {
                        demo3_printer.print2();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        new Thread(){
            public void run(){
                while (true){
                    try {
                        demo3_printer.print3();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
