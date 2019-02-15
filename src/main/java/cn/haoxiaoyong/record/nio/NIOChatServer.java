package cn.haoxiaoyong.record.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by haoxy on 2019/2/15.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class NIOChatServer {

    private Selector selector;

    private ServerSocketChannel listenerChannel;

    private static final int PORT = 9999;

    public NIOChatServer() {

        try {
            //得到选择器
            selector = Selector.open();
            //打开监听通道
            listenerChannel = ServerSocketChannel.open();
            //绑定端口
            listenerChannel.bind(new InetSocketAddress(PORT));
            //设置为非阻塞模式
            listenerChannel.configureBlocking(false);
            //将选择器绑定到监听通道并监听accept事件
            listenerChannel.register(selector, SelectionKey.OP_ACCEPT);
            printInfo("Chat Server is ready....");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (true) {//不断的轮询
                if (selector.select(2000)==0) {
                    printInfo("独自在寒风中等待...");
                    continue;
                }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        //监听到了 accept
                        if (key.isAcceptable()) {//客户端请求连接事件
                            SocketChannel sc = listenerChannel.accept();
                            //设置非阻塞模式
                            sc.configureBlocking(false);
                            //注册到选择器上并监听 read
                            sc.register(selector, SelectionKey.OP_READ);
                            System.out.println(sc.getRemoteAddress().toString().substring(1) + "上线了....");
                        }
                        //监听到了 read
                        if (key.isReadable()) {
                            readMsg(key);//读取客户端发来的数据
                        }
                        iterator.remove();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readMsg(SelectionKey key) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = socketChannel.read(buffer);
            if (count > 0) {
                String msg = new String(buffer.array()).trim();
                printInfo(msg);
                //发广播
                broadCast(socketChannel, msg);
            }
            buffer.clear();
        } catch (IOException e) {
            //当客户端关闭channel时,进行异常的处理
            try {
                printInfo(socketChannel.getRemoteAddress().toString().substring(1) + "下线了..");
                key.cancel();//取消注册
                socketChannel.close();//关闭通道
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    private void broadCast(SocketChannel socketChannel, String msg) throws IOException {
        System.out.println("服务端发送了广播...");
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            SelectableChannel channel = key.channel();
            if (channel instanceof SocketChannel && channel != socketChannel) {
                SocketChannel sc = (SocketChannel) key.channel();
                //把数据存储到缓冲区中
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //往通道中写数据
                sc.write(buffer);
            }
        }
    }

    private void printInfo(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("[" + sdf.format(new Date()) + "]" + str);
    }

    public static void main(String[] args) {
        NIOChatServer chatServer = new NIOChatServer();
        chatServer.start();

    }
}
