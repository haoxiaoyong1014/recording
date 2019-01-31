package cn.haoxiaoyong.record.nio;

/**
 * Created by haoxy on 2019/1/31.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class RequestHandler {
    public String handle(String request) {
        return "From NIOServer Hello " + request + ".\n";
    }
}
