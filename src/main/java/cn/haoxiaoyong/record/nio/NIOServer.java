package cn.haoxiaoyong.record.nio;

import sun.nio.ch.sctp.SctpStdSocketOption;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by haoxy on 2019/1/31.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * telnet localhost 9999
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9999));
        System.out.println("NIO NIOServer has started,listening on port" + serverSocketChannel.getLocalAddress());
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        RequestHandler requestHandler = new RequestHandler();
        while (true) {
            int select = selector.select();
            if (select == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {  //客户端连接请求事件
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel clinetChannel = channel.accept();
                    System.out.println("Connection from " + clinetChannel.getRemoteAddress());
                    clinetChannel.configureBlocking(false);
                    //通过 register改变 channel 要进行的操作
                    clinetChannel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) { //读取客户端数据事件
                    SocketChannel channel = (SocketChannel) key.channel();
                    channel.read(byteBuffer);
                    String request = new String(byteBuffer.array()).trim();
                    byteBuffer.clear();
                    System.out.println(String.format("From %s : %s", channel.getRemoteAddress(), request));
                    String response = requestHandler.handle(request);
                    channel.write(ByteBuffer.wrap(response.getBytes()));
                }
                iterator.remove();
            }
        }
    }
}

