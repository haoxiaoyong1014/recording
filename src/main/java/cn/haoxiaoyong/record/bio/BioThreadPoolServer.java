package cn.haoxiaoyong.record.bio;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by haoxy on 2019/1/29.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
class BioThreadPoolServer {

    //创建一个核心线程数和最大线程数都为5的线程池
    private static ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    //private static ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(9998);
        while (true) {
            System.out.println("没有客户端连接,我阻塞在这里了.....");
            //监听客户端
            Socket accept = serverSocket.accept();
            executorService.execute(new BioServerThreadHandler(accept));
            System.out.println("核心线程数: " + executorService.getCorePoolSize());
            System.out.println("最大线程数: " + executorService.getMaximumPoolSize());
        }
    }

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BioThreadPoolServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
