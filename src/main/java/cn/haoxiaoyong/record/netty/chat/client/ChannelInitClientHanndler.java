package cn.haoxiaoyong.record.netty.chat.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by haoxy on 2019/2/20.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ChannelInitClientHanndler extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //添加一个解码器
        pipeline.addLast("decoder",new StringDecoder())
                //添加一个编码器
                .addLast("encoder",new StringEncoder())
                .addLast("handler",new ChatClientHandler());
    }
}
