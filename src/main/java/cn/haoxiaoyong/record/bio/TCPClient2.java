package cn.haoxiaoyong.record.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;


/**
 * Created by haoxy on 2019/1/28.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * bio 客户端程序
 */
public class TCPClient2 {

    public static void main(String[] args) {

        while (true) {
            //1.创建Socket 对象
            Socket socket = null;
            try {
                socket = new Socket("127.0.0.1", 9999);
                //2.从连接中取出输出流并发消息
                OutputStream outputStream = socket.getOutputStream();
                System.out.println("请输入:");
                Scanner scanner = new Scanner(System.in);
                String msg = scanner.nextLine();
                outputStream.write(msg.getBytes());
                System.out.println("我没有收到服务器的消息,我阻塞在这里了...");
                //3.从连接中取出输入流并接收回话
                InputStream inputStream = socket.getInputStream();//阻塞
                byte[] b = new byte[20];
                inputStream.read(b);
                System.out.println("我收到服务器的消息了");
                System.out.println("老板:" + new String(b).trim());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
