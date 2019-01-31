package cn.haoxiaoyong.record.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by haoxy on 2019/1/29.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class BioServerThreadHandler implements Runnable {

    private Socket socket;

    public BioServerThreadHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //3,从连接中取出输入流来接收消息
        InputStream inputStream = null;
        try {
           // while (true){
                System.out.println("有客户端连接了...");
                inputStream = socket.getInputStream();//阻塞
                System.out.println("但是我没有收到客户端发来的消息,我又阻塞在这里了.....");
                byte[] by = new byte[10];
                inputStream.read(by);
                System.out.println("我收到客户端的消息了...");
                String hostAddress = socket.getInetAddress().getHostAddress();//127.0.0.1
                System.out.println("Connection form"+ socket.getReuseAddress() + "说:" + new String(by).trim());
                //4.从连接中取出输出流并回话
                OutputStream outputStream = socket.getOutputStream();//(4)
                outputStream.write("没钱".getBytes());
           // }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
