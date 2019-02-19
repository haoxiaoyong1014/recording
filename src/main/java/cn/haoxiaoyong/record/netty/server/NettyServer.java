package cn.haoxiaoyong.record.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by haoxy on 2019/2/19.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        //创建一个线程组:用来处理网络事件,(接收客户端连接)
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //创建一个线程组:用来处理网络事件(处理通道IO操作)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //创建服务端启动助手来配置参数
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitHandler());
        //启动服务器端口并绑定端口,等待接收客户端的链接(非阻塞)
        ChannelFuture cf = bootstrap.bind(9999).sync();
        //关闭通道, 关闭线程池
        if (cf.isSuccess()) {
            System.out.println("...Server Netty is Starting...");
        }
        cf.channel().closeFuture().sync();
        bossGroup.shutdownGracefully().syncUninterruptibly();
        System.out.println("Server Close..");
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }
}
