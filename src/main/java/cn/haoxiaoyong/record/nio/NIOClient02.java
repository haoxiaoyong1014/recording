package cn.haoxiaoyong.record.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by haoxy on 2019/2/13.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class NIOClient02 {
    public static void main(String[] args) throws IOException {
        //得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞方式
        socketChannel.configureBlocking(false);
        //提供服务器的IP地址和端口号
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9999);
        //连接服务器端
        if (!socketChannel.connect(address)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("Client: 连接服务端的同时,我还可以干别的事情");
            }
        }
        //得到一个缓冲区并存入数据
        String msg = "hello Server";
        ByteBuffer writeBuf = ByteBuffer.wrap(msg.getBytes());
        //发送数据
        socketChannel.write(writeBuf);
        System.in.read();
    }
}
