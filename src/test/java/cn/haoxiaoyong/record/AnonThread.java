package cn.haoxiaoyong.record;

/**
 * Created by haoxy on 2019/1/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class AnonThread {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " aaaaaaaaaa");
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " bb");
            }
        }).start();
        Thread.currentThread().setName("我是主线程");                    //获取主函数线程的引用,并改名字
        System.out.println(Thread.currentThread().getName());        //获取主函数线程的引用,并获取名字
    }
}
