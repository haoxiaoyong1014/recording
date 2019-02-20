package cn.haoxiaoyong.record.netty.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by haoxy on 2019/2/20.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ChatServer {

    private int port; //服务端端口号

    public ChatServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup boos = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boos, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitHanndler())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture cf = bootstrap.bind(port).sync();
        if (cf.isSuccess()) {
            System.out.println("Netty Chat Server 启动....");
        }
        cf.channel().closeFuture().sync();
        boos.shutdownGracefully();
        work.shutdownGracefully();
        System.out.println("Netty Chat Server 关闭...");
    }

    public static void main(String[] args) throws InterruptedException {
        new ChatServer(8888).run();
    }
}
