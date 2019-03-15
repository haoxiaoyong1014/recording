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

![5771552651783_.pic.jpg](https://upload-images.jianshu.io/upload_images/15181329-7bcae6ecba2af90f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

    ChannelPipeline addFirst(ChannelHandler... handlers)，把一个业务处理类（handler）添加到链中的第一个位置

    ChannelPipeline addLast(ChannelHandler... handlers)，把一个业务处理类（handler）添加到链中的最后一个位置(常用)
