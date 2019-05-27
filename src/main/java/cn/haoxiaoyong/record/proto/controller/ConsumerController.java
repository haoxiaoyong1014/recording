package cn.haoxiaoyong.record.proto.controller;

import cn.haoxiaoyong.record.proto.client.NettyClient;
import cn.haoxiaoyong.record.proto.protobuf.MessageRequestBase;
import cn.haoxiaoyong.record.proto.utils.RestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by Haoxy on 2019-05-27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@RestController
public class ConsumerController {

    @Autowired
    private NettyClient nettyClient;

    @RequestMapping("/send")
    public String send(@RequestBody RestInfo restInfo) {
        MessageRequestBase.MessageRequest message = new MessageRequestBase.MessageRequest()
                .toBuilder()
                .setContent(restInfo.getStr())
                .setRequestId(UUID.randomUUID().toString()).build();
        //异步执行
        nettyClient.sendMsg(message);
        return "send ok";
    }
}
