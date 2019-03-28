#### 手写RPC

**整体分析**

RPC（Remote Procedure Call)，即远程过程调用，它是一种通过网络从远程计算机程序
上请求服务，而不需要了解底层网络实现的技术。常见的RPC 框架有: 源自阿里的Dubbo，
Spring 旗下的Spring Cloud，Google 出品的grpc 等等。

![image.png](https://upload-images.jianshu.io/upload_images/15181329-bb4714192f8fbf17.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

将上面的12个步骤整理为下面9个步骤:

    1,服务消费方(Client)以本地调用方式调用服务
    2. client stub 接收到调用后负责将方法、参数等封装成能够进行网络传输的消息体
    3. client stub 将消息进行编码并发送到服务端
    4. server stub 收到消息后进行解码
    5. server stub 根据解码结果调用本地的服务
    6. 本地服务执行并将结果返回给server stub
    7. server stub 将返回导入结果进行编码并发送至消费方
    8. client stub 接收到消息并进行解码
    9. 服务消费方(client)得到结果
RPC 的目标就是将2-8 这些步骤都封装起来，用户无需关心这些细节，可以像调用本地
方法一样即可完成远程服务调用。接下来我们基于Netty 自己动手搞定一个RPC。

**设计和实现** 

![image.png](https://upload-images.jianshu.io/upload_images/15181329-e6dee691b3ddeda8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240) 

     Client(服务的调用方): 两个接口+ 一个包含main 方法的测试类
     Client Sub: 一个客户端代理类+ 一个客户端业务处理类
     Server(服务的提供方): 两个接口+ 两个实现类
     Server Sub: 一个网络处理服务器+ 一个服务器业务处理类
     注意：服务调用方的接口必须跟服务提供方的接口保持一致（包路径可以不一致）,最终要实现的目标是：在TestNettyRPC 中远程调用HelloRPCImpl 或HelloNettyImpl 中的方法
 
**代码实现** 
 
###### Server(服务的提供方) 

```java
public interface HelloNetty {
    String hello();
}

public class HelloNettyImpl implements HelloNetty {
    @Override
    public String hello() {
        return "----> hello,netty <---";
    }
}

public interface HelloRPC {

    String hello(String name);
}

public class HelloRpcImpl implements HelloRPC {
    @Override
    public String hello(String name) {
        return "hello," + name;
    }
}
```
上述代码作为服务的提供方，我们分别编写了两个接口和两个实现类，供消费方远程调用

###### Server Sub部分

```java
public class ClassInfo implements Serializable {

    private static final long serialVersionUID = -7821682294197810003L;

    private String className;//类名

    private String methodName;//返回值

    private Class<?>[] types; //参数类型

    private Object[] objects; //参数列表
    
        // ..getter
        // ..setter
    }
```     
上述代码作为实体类用来封装消费方发起远程调用时传给服务方的数据

###### 服务器端业务处理类    

```java
public class InvokeHandler extends ChannelInboundHandlerAdapter {

    //得到某接口下某个实现类的名字
    private String getImplClassName(ClassInfo classInfo) throws ClassNotFoundException {
        //服务方接口和实现类所在的包路径
        String interfacePath = "cn.haoxiaoyong.record.rpc.server";
        int lastDot = classInfo.getClassName().lastIndexOf(".");
        String interfaceName = classInfo.getClassName().substring(lastDot);
        Class<?> superClass = Class.forName(interfacePath + interfaceName);
        Reflections reflections = new Reflections(interfacePath);
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
``` 
上述代码作为业务处理类,读取消费方发来的数据,并根据得到的数据进行本地调用,然后把结果返回给消费方.

###### 网络处理服务器

```java
public class NettyRpcServer {
    private int port;

    public NettyRpcServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wprkGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, wprkGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //编码器
                            pipeline.addLast("encoder", new ObjectEncoder())
                                    //解码器
                                    .addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                    .addLast(new InvokeHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("......server is ready......");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            bossGroup.shutdownGracefully();
            wprkGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) {
       new NettyRpcServer(9999).start();
    }
}
```

上述代码是用Netty实现的网络服务器,采用Netty自带的ObjectEncoder 和ObjectDecoder 作为编码解码(为了降低复杂度,这里并没有使用
第三方的编码解码器),当然实际开发中也可以采用JSON或XML.

###### Client Sub部门(客户端业务处理类)

```java

public class ResultHandler extends ChannelInboundHandlerAdapter {

    private Object response;

    public Object getResponse() {
        return response;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg;
        ctx.close();
    }
}
```

上述代码作为客户端的业务处理类读取远程调用返回的数据

###### 客户端代理类

```java
public class NettyRpcProxy {

    //根据结构创建代理对象
    public static Object create(Class target) {
        return Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //封装ClassInfo
                ClassInfo classInfo = new ClassInfo();
                classInfo.setClassName(target.getName());
                classInfo.setMethodName(method.getName());
                classInfo.setObjects(args);
                classInfo.setTypes(method.getParameterTypes());
                //开始用Netty发送数据
                EventLoopGroup group = new NioEventLoopGroup();
                ResultHandler resultHandler = new ResultHandler();
                try {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {

                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    //编码器
                                    pipeline.addLast("encoder", new ObjectEncoder())
                                            //解码器,构造方法第一个参数设置二进制的最大字节数,第二个参数设置具体使用哪个类解析器
                                            .addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                            //客户端业务处理类
                                            .addLast("handler", resultHandler);
                                }
                            });
                    ChannelFuture future = bootstrap.connect("127.0.0.1", 9999).sync();
                    future.channel().writeAndFlush(classInfo).sync();
                    future.channel().closeFuture().sync();
                } finally {
                    group.shutdownGracefully();
                }
                return resultHandler.getResponse();
            }
        });
    }
}

```  
上述代码是用Netty 实现的客户端代理类，采用Netty 自带的ObjectEncoder 和ObjectDecoder
作为编解码器（为了降低复杂度，这里并没有使用第三方的编解码器），当然实际开发时也
可以采用JSON 或XML。  

###### Client

```java
public interface HelloNetty {

    String hello();
}

public interface HelloRPC {

    String hello(String name);
}
```
上述代码定义了两个接口作为服务的调用方

###### Client(服务的调用方-消费方)

```java
public class TestNettyRpc {
    public static void main(String[] args) {
        //第一次远程调用
        HelloNetty helloNetty = (HelloNetty) NettyRpcProxy.create(HelloNetty.class);
        System.out.println(helloNetty.hello());

        //第二次远程调用
        HelloRPC helloRPC = (HelloRPC) NettyRpcProxy.create(HelloRPC.class);
        System.out.println(helloRPC.hello("RPC"));
    }
}
``` 
消费方不需要知道底层的网络实现细节，就像调用本地方法一样成功发起了两次远程调用。

**测试结果:**

![image.png](https://upload-images.jianshu.io/upload_images/15181329-2c8dface3b6daa6e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**使用方式:**

1,首先启动服务方: <a href="https://github.com/haoxiaoyong1014/recording/blob/master/src/main/java/cn/haoxiaoyong/record/rpc/serverSub/NettyRpcServer.java">NettyRpcServer</a>
中的main方法;

2,然后启动服务调用方: <a href="https://github.com/haoxiaoyong1014/recording/blob/master/src/main/java/cn/haoxiaoyong/record/rpc/client/TestNettyRpc.java">TestNettyRpc</a>中的测试方法;