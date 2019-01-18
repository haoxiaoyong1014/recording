package cn.haoxiaoyong.record.thread2;

/**
 * Created by haoxy on 2019/1/17.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class TestBusiness {

    public static void main(String[] args) {
        final Business business = new Business();
        new Thread(new Runnable() {
            public void run() {
                threadExecute(business, "sub");
            }
        }).start();
        threadExecute(business, "main");
    }
    public static void threadExecute(Business business, String threadType) {
        for(int i = 0; i < 100; i++) {
            try {
                if("main".equals(threadType)) {
                    business.main(i);
                } else {
                    business.sub(i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
