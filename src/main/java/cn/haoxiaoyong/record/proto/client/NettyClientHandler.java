package cn.haoxiaoyong.record.proto.client;

import cn.haoxiaoyong.record.proto.protobuf.MessageResponseBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Haoxy on 2019-05-27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageResponseBase.MessageResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageResponseBase.MessageResponse msg) throws Exception {
        log.info("客户端收到消息：{}", msg.toString());
    }

    /**
     * 处理异常, 一般将实现异常处理逻辑的Handler放在ChannelPipeline的最后
     * 这样确保所有入站消息都总是被处理，无论它们发生在什么位置，下面只是简单的关闭Channel并打印异常信息
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
