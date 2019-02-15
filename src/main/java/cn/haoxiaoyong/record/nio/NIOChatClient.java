package cn.haoxiaoyong.record.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by haoxy on 2019/2/15.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class NIOChatClient {

    private final String HOST = "127.0.0.1";

    private int PORT = 9999;

    private String userName;

    private Selector selector;

    private SocketChannel socketChannel;

    public NIOChatClient() throws IOException {

        /*//得到选择器
        selector = Selector.open();*/
        //连接远程服务器
        socketChannel = SocketChannel.open();
        //设置非阻塞
        socketChannel.configureBlocking(false);
      /*  //注册选择器并设置为 read
        socketChannel.register(selector, SelectionKey.OP_READ);*/
        //提供服务器端的IP和端口号
        InetSocketAddress address = new InetSocketAddress(HOST, PORT);
        //连接服务器
        if (!socketChannel.connect(address)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("没有人找我聊天,我去玩了....");
            }
        }
        //得到客户端的IP地址和端口信息,作为聊天用户名使用
        userName = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println("----Client(" + userName + ")is ready -----");
    }

    //向服务端发送数据
    public void sendMsg(String msg) throws IOException {
        //如果控制台输入的 bye 就关闭通道,结束聊天
        if (msg.equalsIgnoreCase("bye")) {
            socketChannel.close();
            socketChannel = null;
            return;
        }
        msg = userName + "说:" + msg;
        //往通道中写数据
        socketChannel.write(ByteBuffer.wrap(msg.getBytes()));

    }

    //从服务端接收数据
    public void receiveMsg() throws IOException {
        //得到一个缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //读取数据并存储到缓冲区
        int size = socketChannel.read(buffer);
        if (size > 0) {
            String msg = new String(buffer.array());
            System.out.println(msg.trim());
        }
    }
    //从服务端接收数据
    /*public void receiveMsg() throws IOException {

        if (selector.select(2000) == 0) {//有可用通道
            System.out.println("人呢？都去哪儿了？没人聊天啊...");
        }
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            if (key.isReadable()) {
                //得到关联的通道
                SocketChannel socketChannel = (SocketChannel) key.channel();
                //得到一个缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                //读取数据并存储到缓冲区
                socketChannel.read(buffer);
                //把缓冲区数据转换成字符串
                String msg = new String(buffer.array());
                //msg = userName + "说" + msg;
                System.out.println(msg.trim());
            }
            iterator.remove();
        }

    }*/

}
