package cn.haoxiaoyong.record.netty.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by haoxy on 2019/2/19.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {

        //创建一个EventLoopGroup线程组
        EventLoopGroup group = new NioEventLoopGroup();
        //创建一个客户端启动助手
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitHanndler());

        //启动客户端.等待连接上服务器端(非阻塞)
        ChannelFuture cf = bootstrap.connect("127.0.0.1", 9999).sync();
        if(cf.isSuccess()){
            System.out.println("Client Netty is Starting....");
        }
        cf.channel().closeFuture().sync();
        group.shutdownGracefully().syncUninterruptibly();
    }
}
