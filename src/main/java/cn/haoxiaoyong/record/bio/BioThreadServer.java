package cn.haoxiaoyong.record.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by haoxy on 2019/1/29.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class BioThreadServer {

    public static void start() throws IOException {
// 1,创建ServerSocket对象
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(9998);
        while (true) {
            System.out.println("没有客户端连接,我阻塞在这里了.....");
            //2,监听客户端
            Socket accept = serverSocket.accept();//阻塞,
            Thread thread = new Thread(new BioServerThreadHandler(accept));//.start();
            thread.start();
            System.out.println(thread.getName());

        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BioThreadServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

