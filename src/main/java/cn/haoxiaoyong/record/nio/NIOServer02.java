package cn.haoxiaoyong.record.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by haoxy on 2019/2/13.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class NIOServer02 {

    public static void main(String[] args) throws IOException {
        //得到一个ServerSocketChanel对象
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //得到一个selector对象
        Selector selector = Selector.open();
        //绑定一个端口号
        serverSocketChannel.bind(new InetSocketAddress(9999));
        //设置非阻塞方式
        serverSocketChannel.configureBlocking(false);
        //把ServerSocketChannel对象注册给Selector对象
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            //监控客户端
            if (selector.select(2000) == 0) {
                System.out.println("Server:没有客户端搭理我,我就干点别的事");
                continue;
            }
            //得到SelectionKey,判断通道里的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {//客户端连接请求
                    System.out.println("OP_ACCEPT");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {//读取客户端数据事件
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    socketChannel.read(byteBuffer);
                    System.out.println("客户端发来数据:"+new String(byteBuffer.array()));
                }
                //手动从集合中移除当前 key,防止重复处理
                keyIterator.remove();
            }
        }

    }
}
