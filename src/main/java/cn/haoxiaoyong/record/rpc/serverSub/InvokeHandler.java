package cn.haoxiaoyong.record.rpc.serverSub;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

public class InvokeHandler extends ChannelInboundHandlerAdapter {

    //得到某接口下某个实现类的名字
    private String getImplClassName(ClassInfo classInfo) throws ClassNotFoundException {
        //服务方接口和实现类所在的包路径
        String interfacePath = "cn.haoxiaoyong.record.rpc.server";
        int lastDot = classInfo.getClassName().lastIndexOf(".");
        String interfaceName = classInfo.getClassName().substring(lastDot);//.HelloNetty
        Class<?> superClass = Class.forName(interfacePath + interfaceName);//interface cn.haoxiaoyong.record.rpc.server.HelloNetty
        Reflections reflections = new Reflections(interfacePath);//这里用到了 pom.xml中的依赖项
        //得到某接口下的所有实现类
        Set<Class<?>> ImplClassSet = (Set<Class<?>>) reflections.getSubTypesOf(superClass);
        if (ImplClassSet.size() == 0) {
            System.out.println("未找到实现类");
            return null;
        } else if (ImplClassSet.size() > 1) {
            System.out.println("找到多个实现类，未明确使用哪一个");
            return null;
        } else {
            //把集合转换为数组
            Class[] classes = ImplClassSet.toArray(new Class[0]);
            return classes[0].getName();//得到实现类的名字
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClassInfo classInfo = (ClassInfo) msg;
        Object clazz = Class.forName(getImplClassName(classInfo)).newInstance();
        Method method = clazz.getClass().getMethod(classInfo.getMethodName(), classInfo.getTypes());
        //通过反射调用实现类的方法
        Object result = method.invoke(clazz, classInfo.getObjects());
        ctx.writeAndFlush(result);
    }
}
