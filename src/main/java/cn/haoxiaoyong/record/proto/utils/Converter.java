package cn.haoxiaoyong.record.proto.utils;

import cn.haoxiaoyong.record.proto.protobuf.MessageRequestBase;
import org.springframework.stereotype.Component;

/**
 * Created by Haoxy on 2019-05-27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Component
public class Converter {

    public String converterTask(MessageRequestBase.MessageRequest messageRequest) {
        //做一些业务处理....
        String content = messageRequest.getContent();
        return "Hello " + content;
    }
}
