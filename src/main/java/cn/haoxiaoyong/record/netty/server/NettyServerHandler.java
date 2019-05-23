package cn.haoxiaoyong.record.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * Created by haoxy on 2019/2/19.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    //监听到客户端连接事件
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server: handlerAdded");
    }

    //监听到客户端活跃
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server: channelActive");
    }


    //服务端读取
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server:" + ctx);
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发来的消息 :"+buf.toString(CharsetUtil.UTF_8));
    }

    //服务端读取数据完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("你说的啥,我没有听见",CharsetUtil.UTF_8));
    }

    //发生异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
