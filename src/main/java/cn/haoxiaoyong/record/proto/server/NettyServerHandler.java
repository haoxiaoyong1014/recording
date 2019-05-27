package cn.haoxiaoyong.record.proto.server;

import cn.haoxiaoyong.record.proto.protobuf.MessageRequestBase;
import cn.haoxiaoyong.record.proto.protobuf.MessageResponseBase;
import cn.haoxiaoyong.record.proto.utils.Converter;
import cn.haoxiaoyong.record.proto.utils.SpringUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Haoxy on 2019-05-27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageRequestBase.MessageRequest> {

    private static Converter converter;

    static {
        converter = SpringUtil.getBean(Converter.class);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestBase.MessageRequest msg) throws Exception {
        if (msg.getContent() != null) {
            log.info("收到客户端的业务消息：{}",msg.toString());
            String converter = NettyServerHandler.converter.converterTask(msg);
            log.info("server 拿到处理后的返回结果 :{}",converter);
            MessageResponseBase.MessageResponse response = MessageResponseBase.MessageResponse.newBuilder()
                    .setCode(200)
                    .setContent(converter)
                    .setMsg("success")
                    .build();
            ctx.writeAndFlush(response);
        }
    }
}
