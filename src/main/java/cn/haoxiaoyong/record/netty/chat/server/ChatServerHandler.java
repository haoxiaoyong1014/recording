package cn.haoxiaoyong.record.netty.chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.*;

/**
 * Created by haoxy on 2019/2/20.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    //声明一个list集合,存放客户端的连接

    private static List<Channel> channels = new ArrayList<Channel>();


    //读取客户端数据事件
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Channel channel = ctx.channel();
        for (Channel inchannel : channels) {
            //排除当前通道
            if (channel != inchannel) {
                inchannel.writeAndFlush("[" + channel.remoteAddress().toString().substring(1) + "]说: " + msg
                        + "\n");
            }
        }
    }

    //通道就绪事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[Server:]" + channel.remoteAddress().toString().substring(1) + "上线了..");
    }

    //通道未就绪事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String address = ctx.channel().remoteAddress().toString().substring(1);
        channels.remove(ctx.channel());
        System.out.println(address + " 离线了...");
    }

    //发生异常事件
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[Server]:"+channel.remoteAddress().toString().substring(1)+" 异常");
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        int size = channels.size();
        System.out.println("当前在线人数: " + size);
    }
}
