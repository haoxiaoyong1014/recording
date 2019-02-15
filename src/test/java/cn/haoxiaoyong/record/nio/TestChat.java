package cn.haoxiaoyong.record.nio;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by haoxy on 2019/2/15.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class TestChat {

    public static void main(String[] args) throws IOException {
        //创建一个聊天客户端对象
        NIOChatClient chatClient=new NIOChatClient();
        new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        chatClient.receiveMsg();
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        Scanner scanner=new Scanner(System.in);
        //在控制台输入数据并发送到服务端
        while (scanner.hasNextLine()){
            String msg = scanner.nextLine();
            chatClient.sendMsg(msg);
        }
    }
}
