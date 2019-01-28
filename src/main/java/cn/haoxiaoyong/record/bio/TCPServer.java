package cn.haoxiaoyong.record.bio;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by haoxy on 2019/1/28.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * bio 服务端程序
 */
public class TCPServer {

    public static void main(String[] args) throws Exception {
        // 1,创建ServerSocket对象
        ServerSocket serverSocket = new ServerSocket(9999);
        while (true) {

            System.out.println("没有客户端连接,我阻塞在这里了.....");
            //2,监听客户端
            Socket accept = serverSocket.accept();//阻塞,
            System.out.println("有客户端连接了...");
            //3,从连接中取出输入流来接收消息
            InputStream inputStream = accept.getInputStream();//阻塞
            System.out.println("但是我没有收到客户端发来的消息,我又阻塞在这里了.....");
            byte[] by = new byte[10];
            inputStream.read(by);
            System.out.println("我收到客户端的消息了...");
            String hostAddress = accept.getInetAddress().getHostAddress();
            System.out.println(hostAddress + "说:" + new String(by).trim());
            //4.从连接中取出输出流并回话
            OutputStream outputStream = accept.getOutputStream();//(4)
            outputStream.write("没钱".getBytes());
            accept.close();
        }
    }
}
