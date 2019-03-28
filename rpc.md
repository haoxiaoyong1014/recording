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
     Client Stub: 一个客户端代理类+ 一个客户端业务处理类
     Server(服务的提供方): 两个接口+ 两个实现类
     Server Stub: 一个网络处理服务器+ 一个服务器业务处理类
     注意：服务调用方的接口必须跟服务提供方的接口保持一致（包路径可以不一致）,最终要实现的目标是：在TestNettyRPC 中远程调用HelloRPCImpl 或HelloNettyImpl 中的方法
 
     
     
      