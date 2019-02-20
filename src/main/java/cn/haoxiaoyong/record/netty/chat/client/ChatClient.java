package cn.haoxiaoyong.record.netty.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

/**
 * Created by haoxy on 2019/2/20.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ChatClient {

    private final String host;

    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void run() throws InterruptedException {
        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitClientHanndler());
        ChannelFuture cf = bootstrap.connect(host, port).sync();
        System.out.println("-----"+cf.channel().localAddress().toString().substring(1)+"----");
        if(cf.isSuccess()){
            System.out.println("Netty Client start...");
        }
        Scanner scanner=new Scanner(System.in);
        while (scanner.hasNextLine()){
            String msg = scanner.nextLine();
            cf.channel().writeAndFlush(msg+"\r\n");
        }
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        new ChatClient("127.0.0.1",8888).run();
    }
}
