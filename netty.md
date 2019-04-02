#### 概述

#### 单线程模型: 
用户发起IO请求到Reactor线程,
Ractor线程将用户的IO请求放入到通道，然后再进行后续处理,
处理完成后，Reactor线程重新获得控制权，继续其他客户端的处理

![image.png](https://upload-images.jianshu.io/upload_images/15181329-e21f2228993119c9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
 
 作为服务端他接收客户端的连接,作为客户端他会向服务端发起连接,同时他可以读取请求和响应消息,发送请求,异步非阻塞,从他的整体架构来看,他是可以完成我们所有的IO操作的,这只局限于小型的应用场景,但是在高负载,高并发的情况下使用单线程肯定是不行的,很多的客户端过来就会造成超时,客户端都会超时机制,会继续重新连接,这样就会造成一个死循环,严重就会造成单节点故障甚至宕机.
 
  *  1.但单线程的Reactor模型每一个用户事件都在一个线程中执行：
  *  2.性能有极限，不能处理成百上千的事件
  *  3.当负荷达到一定程度时，性能将会下降
  *  4.某一个事件处理器发生故障，不能继续处理其他事件
 
 举一个现实生活中例子:
 
 例如我开了一店,但是这个店我只招了一个人,这个人要做的事情要接待客人,到了店了之后要给客人端茶倒水,但是我的客人多了起来,他一个人肯定是忙不过来的,
 
 
#### 多线程模型
Reactor多线程模型是由一组NIO线程来处理IO操作（之前是单个线程），所以在请求处理上会比上一中模型效率更高，可以处理更多的客户端请求。
![image.png](https://upload-images.jianshu.io/upload_images/15181329-737542dedaf18971.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
 
 左边这一块Reactor单线程主要作用是处理客户端的连接,然后将连接丢到后面的线程池中,后面的线程池主要就是进行读写请求,
 
 还是上面的一个例子:
 
 我招了一个人在前台接待客人,接待完之后呢我就不管了,交给后面的人去端茶倒水,后面有跟多的人来服务,这样就大大增加了我的一个并发量,但是呢如果达到了一个百万级别的并发量,就是说我们一下子有很多很多的客人,前台的一个人肯定是招待不过来的
 
#### 主从线程模型
 
 一组的线程池接收请求,一组线程池来处理io
 
![image.png](https://upload-images.jianshu.io/upload_images/15181329-5587e7ef5d765535.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这种线程模型是Netty推荐使用的线程模型,这种模型适用于高并发场景，一组线程池接收请求，一组线程池处理IO。


#### 核心API介绍:

**ChannelHandler及其实现类**

ChannelHandler 接口定义了许多事件处理的方法，我们可以通过重写这些方法去实现具体的业务逻辑。API 关系如下图所示：

![5761552648514_.pic_hd.jpg](https://upload-images.jianshu.io/upload_images/15181329-30857f88a29349ef.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我们经常需要定义一个Handler类去继承ChannelInboundHandlerAdapter,然后通过重写相应的方法实现业务的逻辑,我们接下来看看一般
都需要重写哪些方法:

    1,public void channelActive(ChannelHanndlerContext ctx),通道就绪事件(channel为活跃状态)
    
    2,public void channelInactive(ChannelHanndlerContext ctx),通道就绪事件(channel为活不跃状态,但客户端和服务端端口之后)

    3,public void channelRead(ChannelHandlerContext ctx, Object msg),通道读取数据事件

    4,public void channelReadComplete(ChannelHandlerContext ctx),数据读取完毕事件
    
    5,public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause),通道发生异常事件
    
    6,public void handlerAdded(ChannelHandlerContext ctx), 助手类添加(Handler类添加),当有新的客户端连接服务器之后,就会自动调用这个方法 
    
    7,public void handlerRemoved(ChannelHandlerContext ctx)助手类移除(Handler类移除)
    

执行顺序:  6,1,3,4,2,7
        
**Pipeline 和ChannelPipeline**

ChannelPipeline 是一个Handler的集合,它负责处理和拦截inbound或者outbound的事件和操作,相当于一个贯穿Netty的链.
每一个ChannelHandler都会有一个ChannelHandlerContext(上下文对应),下面会介绍到`ChannelHandlerContext`

![5771552651783_.pic.jpg](https://upload-images.jianshu.io/upload_images/15181329-7bcae6ecba2af90f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

    ChannelPipeline addFirst(ChannelHandler... handlers)，把一个业务处理类（handler）添加到链中的第一个位置

    ChannelPipeline addLast(ChannelHandler... handlers)，把一个业务处理类（handler）添加到链中的最后一个位置(常用)

**ChannelHandlerContext**

这是事件处理上下文对象,Pipeline链中的实际处理节点.每个处理节点ChannelHandlerContext 中包含一个具体的事件处理器ChannelHandler ， 同时                             
ChannelHandlerContext 中也绑定了对应的pipeline 和Channel 的信息，方便对ChannelHandler进行调用。常用方法如下所示：

    ChannelFuture close(),关闭通道
    
    ChannelOutboundInvoker flush(),刷新
    
    ChannelFuture writeAndFlush(Object msg),将数据写到ChannelPipeline 中当前ChannelHandler 的下一个ChannelHandler 开始处理（出站）  
    
    Channel channel(), 获取当前通道,之后可以通过当前通道获取对应通道ID包括(长ID和短ID),例如: channelHandlerContext.channel().id().asLongText(),
    
**ChannelOption**

Netty在创建Channel实例后,一般都需要设置ChannelOption参数,ChannelOption是Socket的标准参数,而非Netty独创的,常用参数
配置有:

  * ChannelOption.SO_BACKLOG
    
        对应TCP/IP协议listen函数中的 backlog参数,用来初始化服务器可连接队列大小,服务端处理客户端连接请求是顺序处理的
        ,所以同一时间只能处理一个客户端连接,多个客户端来的时候,服务端将不能处理的客户端连接请求放到队列中等待处理,backlog
        参数指定了队列大小.
        
  * ChannelOption.SO_KEEPALIVE,一直保持连接活动状态    

**ChannelFuture**

表示Channel 中异步I/O 操作的结果，在Netty 中所有的I/O 操作都是异步的，I/O 的调用会直接返回，调用者并不能立刻获得结果，但是可以通过ChannelFuture 来获取I/O 操作的处理状态.

常用方法如下所示：

    Channel channel(),返回当前正在进行IO 操作的通道
    
    ChannelFuture sync(),等待异步操作执行完毕
    
    ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener),添加监听器
    
    ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener),移除监听
    
    boolean isSuccess(),当且仅当I/O操作完成时,返回true
    
    
**EventLoopGroup 和其实现类NioEventLoopGroup**    

EventLoopGroup 是一组EventLoop 的抽象，Netty 为了更好的利用多核CPU 资源，一般会有多个EventLoop 同时工作，每个EventLoop 维护着一个Selector 实例。
EventLoopGroup 提供next 接口，可以从组里面按照一定规则获取其中一个EventLoop来处理任务。在Netty 服务器端编程中，我们一般都需要提供两个EventLoopGroup，例如：

BossEventLoopGroup 和WorkerEventLoopGroup。

通常一个服务端口即一个ServerSocketChannel 对应一个Selector 和一个EventLoop 线程。BossEventLoop 负责接收客户端的连接并将SocketChannel 交给WorkerEventLoopGroup 来进
行IO 处理，如下图所示：
  
  
![image.png](https://upload-images.jianshu.io/upload_images/15181329-f70b09c97d3d1bee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

BossEventLoopGroup 通常是一个单线程的EventLoop，EventLoop 维护着一个注册了ServerSocketChannel 的Selector 实例，BossEventLoop 不断轮询Selector 将连接事件分离出来，
通常是OP_ACCEPT 事件，然后将接收到的SocketChannel 交给WorkerEventLoopGroup，WorkerEventLoopGroup 会由next 选择其中一个EventLoopGroup 来将这个SocketChannel 注
册到其维护的Selector 并对其后续的IO 事件进行处理。

常用方法如下所示:

    public NioEventLoopGroup(),构造方法
    
    public Future<?> shutdownGracefully(),断开连接,关闭线程
    
**ServerBootstrap和Bootstrap** 

ServerBootstrap是Netty中的服务器端启动助手,通过它可以完成服务器端的各种配置;Bootstrap是Netty中的客户端启动助手;
通过它可以完成客户端的各种配置,常用方法如下:

     public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup),该方法用于服务器端，用来设置两个EventLoop
     
     public B group(EventLoopGroup group),该方法用于客户端，用来设置一个EventLoop
     
     public B channel(Class<? extends C> channelClass),该方法用来设置一个服务器端的通道实现
     
     public <T> B option(ChannelOption<T> option, T value),用来给ServerChannel 添加配置
     
     public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value),用来给接收到的通道添加配置
     
     public ServerBootstrap childHandler(ChannelHandler childHandler),该方法用来设置业务处理类（自定义的handler）
     
     public ChannelFuture bind(int inetPort) ，该方法用于服务器端，用来设置占用的端口号
     
     public ChannelFuture connect(String inetHost, int inetPort) ，该方法用于客户端，用来连接服务器端 
  
**Unpooled 类**   

这是Netty 提供的一个专门用来操作缓冲区的工具类，常用方法如下所示:

    public static ByteBuf copiedBuffer(CharSequence string, Charset charset)，通过给定的数据
    和字符编码返回一个ByteBuf 对象（类似于NIO 中的ByteBuffer 对象）
  
      
#### 入门案例   


**添加依赖**

```xml
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.21.Final</version>
        </dependency>
```
**NettyServer(服务端)**
```java
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        //创建一个线程组:用来处理网络事件,(接收客户端连接)
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //创建一个线程组:用来处理网络事件(处理通道IO操作)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //创建服务端启动助手来配置参数
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitHandler());
        //启动服务器端口并绑定端口,等待接收客户端的链接(非阻塞)
        ChannelFuture cf = bootstrap.bind(9999).sync();
        //关闭通道, 关闭线程池
        if (cf.isSuccess()) {
            System.out.println("...Server Netty is Starting...");
        }
        cf.channel().closeFuture().sync();
        bossGroup.shutdownGracefully().syncUninterruptibly();
        System.out.println("Server Close..");
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }
}

```
**ChannelInitHandler(服务端通道初始化对象)**
```java
public class ChannelInitHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //往Pipline链中添加自定义的业务处理 handler
        pipeline.addLast(new NettyServerHandler());  //服务器端业务处理类
        System.out.println("...Server is ready..");
    }
}
```
**NettyServerHandler(自定义服务器端业务处理类)**
```java
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
```
**NettyClient(客户端)**

```java
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {

        //创建一个EventLoopGroup线程组
        EventLoopGroup group = new NioEventLoopGroup();
        //创建一个客户端启动助手
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitHanndler());

        //启动客户端.等待连接上服务器端(非阻塞)
        ChannelFuture cf = bootstrap.connect("127.0.0.1", 9999).sync();
        if(cf.isSuccess()){
            System.out.println("Client Netty is Starting....");
        }
        cf.channel().closeFuture().sync();
        group.shutdownGracefully().syncUninterruptibly();

    }
}
```   

**ChannelInitHanndler(客户端通道初始化对象)**
```java
public class ChannelInitHanndler extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new NettyClientHandler());
        System.out.println("......Client is ready.......");
    }
}
``` 
**NettyClientHandler(自定义客户端业务处理类)**
```java
public class NettyClientHandler extends ChannelInboundHandlerAdapter {


    //监听到链接
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client: handlerAdded...");

    }

    //通道就绪事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client: channelActive...");
        System.out.println("Client: "+ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("老板,还钱吧",CharsetUtil.UTF_8));

    }

    //读取数据事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务器端发来的消息: "+buf.toString(CharsetUtil.UTF_8));

    }

    //数据读取完毕事件
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    //异常发生事件
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client Close");
    }
}
```

#### 网络聊天案例

就不贴出代码了:

<a href="https://github.com/haoxiaoyong1014/recording/tree/master/src/main/java/cn/haoxiaoyong/record/netty/chat">具体代码点击这里->chat</a>

![image.png](https://upload-images.jianshu.io/upload_images/15181329-47162642f87c281d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 整合WebSocket

Netty结合webSocket做简单的聊天案例

案例效果图:

窗口A给窗口B发送消息:
![5791552706434_.pic.jpg](https://upload-images.jianshu.io/upload_images/15181329-435eab3ddc05f7ed.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

窗口B回复窗口A的消息
![5801552706540_.pic.jpg](https://upload-images.jianshu.io/upload_images/15181329-8a302f144acab26e.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这里只列出前端代码:

```html
<body>
<input type="text" id="message">
<input type="button" value="发送消息" onclick="sendMsg()">
<br/>
接收到消息:
<p id="server_message" style="background-color: #AAAAAA"></p>

<script>
    var websocket = null;
    //判断当前浏览器是否支持 webSocket
    if (window.WebSocket) {
        websocket = new WebSocket("ws://127.0.0.1:9001/ws");
        websocket.onopen = function (ev) {
            console.log("建立连接");
        }
        websocket.onclose = function (ev) {
            console.log("断开连接");
        }
        websocket.onmessage = function (ev) {
            console.log("接收到服务器的消息" + ev.data);
            var server_message = document.getElementById("server_message");
            server_message.innerHTML += ev.data + "<br/>";
        }
    } else {
        alert("当前浏览器不支持 webSocket")
    }

    function sendMsg() {
        var message = document.getElementById("message");
        websocket.send(message.value)
    }
</script>
</body>
```
后端代码有详细的注释,具体看案例中的代码; 

**后端代码地址: <a href="https://github.com/haoxiaoyong1014/netty-chat">netty-chat</a>**


#### 使用netty做心跳检测

<a href="https://github.com/haoxiaoyong1014/springboot-netty">springboot整合netty做心跳检测</a>
  
  
#### 使用netty做文件传输

<a href="https://github.com/haoxiaoyong1014/netty-file">使用netty做文件传输</a>  

#### netty编码解码

**概述**

    我们在编写网络应用程序的时候需要注意codec(编解码器),因为数据在网络中传输的都是二进制字节码数据,而我们拿到的目标数据往往
    不是字节码数据,因此在发送数据时就需要编码,收到数据时需要解码
    codec 的组成部分有两个：decoder(解码器)和encoder(编码器)。encoder 负责把业务数据转换成字节码数据，decoder 负责把字节码数据转换成业务数据。
    其实Java 的序列化技术就可以作为codec 去使用，但是它的硬伤太多：
        1. 无法跨语言，这应该是Java 序列化最致命的问题了。
        2. 序列化后的体积太大，是二进制编码的5 倍多。
        3. 序列化性能太低。
    由于Java 序列化技术硬伤太多，因此Netty 自身提供了一些codec，如下所示：
    Netty 提供的解码器：
        1. StringDecoder, 对字符串数据进行解码
        2. ObjectDecoder，对Java 对象进行解码
    Netty 提供的编码器:
        1. StringEncoder，对字符串数据进行编码
        2. ObjectEncoder，对Java 对象进行编码
    Netty 本身自带的ObjectDecoder 和ObjectEncoder 可以用来实现POJO 对象或各种业务对象的编码和解码，但其内部使用的仍是Java 序列化技术，所以我们不建议使用。因此对
    于POJO 对象或各种业务对象要实现编码和解码，我们需要更高效更强的技术。  
    
**Google 的Protocol(Google出品必然牛x)**  

Protocol 是Google 发布的开源项目，全称Google Protocol Buffers，特点如下：
* 支持跨平台、多语言（支持目前绝大多数语言，例如C++、C#、Java、python 等）
* 高性能，高可靠性
* 使用Protocol 编译器能自动生成代码，Protocol 是将类的定义使用.proto 文件进行描述，然后通过protoc.exe 编译器根据.proto 自动生成.java 文件

1, <a href="https://github.com/haoxiaoyong1014/recording/blob/master/proto.md">首先是安装</a>

2, 使用:

   * 2.1 引入Protocol的坐标
 ```xml
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>3.6.1</version>
        </dependency>
```

 * 2.2定义自己的协议格式
   
  接着是需要按照官方要求的语法定义自己的协议格式。

  比如我这里需要定义一个输入输出的报文格式：
  
  BaseRequest.proto:
    
        syntax = "proto3"; //(1)
        option java_outer_classname = "BaseRequest";//(2)
        message RequestInfo { //(3)
            int32 id = 1; //(4)
            string appid = 2;
        }
 (1): 版本号 
   
 (2): 设置生成的Java类名  
 
 (3): 内部类的类名,真正的POJO
 
 (4): 设置类中的属性,符号后是序号,不是属性值 
 
注意个文件名`BaseRequest.proto`必须是 .proto后缀

  BaseResponse.proto:
  
        syntax = "proto3";
        option java_outer_classname = "BaseResponse";
        message ResponseInfo {
            int32 id = 1;
            string name = 2;
            string responseMessage=3;
        }
 以上同理
* 2.3 通过protoc.exe 根据描述文件生成Java 类，具体操作如下所示：

    * 进入 `BaseRequest.proto`,`BaseRequest.proto`这两个文件所在的目录;
    * 执行 `protoc --java_out=/tmp BaseResponse.proto BaseRequest.proto`,生成的 java类就会在 /tmp 文件夹下;当然这个目录可以改为其他的.
    * 将这两个 java 类拷贝到项目中,这两个类我们不要编辑它，直接拿着用即可，该类内部有一个内部类，这个内部类才是真正的POJO，一定要注意。

* 2.4 在 netty 中使用  
**Client**
![image.png](https://upload-images.jianshu.io/upload_images/15181329-58f2117f27c4d2ce.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
上述代码在编写客户端程序时，要向Pipeline 链中添加ProtobufEncoder 编码器对象。

![image.png](https://upload-images.jianshu.io/upload_images/15181329-388859943cdab4af.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
上述代码在往服务器端发送（POJO）时就可以使用生成的BaseRequest 类搞定，非常方便。

**Server**

![image.png](https://upload-images.jianshu.io/upload_images/15181329-ad500ed55c41042f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
上述代码在编写服务器端程序时，要向Pipeline 链中添加ProtobufDecoder 解码器对象。

![image.png](https://upload-images.jianshu.io/upload_images/15181329-17ffeee31366b57b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
上述代码在服务器端接收数据时,直接就可以把数据转换成 pojo 使用,非常方便

至此使用Google 的Protocol就结束了...

#### netty粘包拆包 

**什么是粘包、拆包？**
在基于流的传输里比如TCP/IP，接收到的数据会先被存储到一个socket接收缓冲里。不幸的是，基于流的传输并不是一个数据包队列，而是一个字节队列。即使你发送了2个独立的数据包，操作系统也不会作为2个消息处理而仅仅是作为一连串的字节而言。
因此这是不能保证你远程写入的数据就会准确地读取。举个例子，让我们假设操作系统的TCP/TP协议栈已经接收了3个数据包：
  
![image.png](https://upload-images.jianshu.io/upload_images/15181329-152e53d3cf533bab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
 
由于基于流传输的协议的这种普通的性质，在你的应用程序里读取数据的时候会有很高的可能性被分成下面的片段

![image.png](https://upload-images.jianshu.io/upload_images/15181329-c2ee5f5a27c60f83.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

因此，一个接收方不管他是客户端还是服务端，都应该把接收到的数据整理成一个或者多个更有意思并且能够让程序的业务逻辑更好理解的数据。在上面的例子中，接收到的数据应该被构造成下面的格式：

![image.png](https://upload-images.jianshu.io/upload_images/15181329-152e53d3cf533bab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**解决方法由如下几种：**

1、消息定长，报文大小固定长度，例如每个报文的长度固定为200字节，如果不够空位补空格；

2、包尾添加特殊分隔符，例如每条报文结束都添加回车换行符（例如FTP协议）或者指定特殊字符作为报文分隔符，接收方通过特殊分隔符切分报文区分；

3、将消息分为消息头和消息体，消息头中包含表示信息的总长度（或者消息体长度）的字段；


Netty提供了多个解码器，可以进行分包的操作，分别是：

    LineBasedFrameDecoder

    DelimiterBasedFrameDecoder（添加特殊分隔符报文来分包）

    FixedLengthFrameDecoder（使用定长的报文来分包）

    LengthFieldBasedFrameDecoder   
    
以上都是生产中不经常使用的.  
  
接下来我们使用Protocol 来解决,拆、粘包,用Protocol来解决拆、粘包那是相当简单的.
只需要在服务端和客户端加上这两个编解码工具即可:
```java
//拆包解码
.addLast(new ProtobufVarint32FrameDecoder())
.addLast(new ProtobufVarint32LengthFieldPrepender())
```
这个编解码工具可以简单理解为是在消息体中加了一个 32 位长度的整形字段，用于表明当前消息长度。
                     