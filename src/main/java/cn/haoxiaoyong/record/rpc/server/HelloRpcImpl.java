package cn.haoxiaoyong.record.rpc.server;

public class HelloRpcImpl implements HelloRpc {
    @Override
    public String hello(String name) {
        return "hello," + name;
    }
}
