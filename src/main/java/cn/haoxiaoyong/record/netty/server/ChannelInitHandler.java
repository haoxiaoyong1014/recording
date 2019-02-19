package cn.haoxiaoyong.record.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * Created by haoxy on 2019/2/19.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * 创建一个通道初始化对象
 */
public class ChannelInitHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //往Pipline链中添加自定义的业务处理 handler
        pipeline.addLast(new NettyServerHandler());  //服务器端业务处理类
        System.out.println("...Server is ready..");
    }
}
