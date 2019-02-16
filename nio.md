### NIO编程(同步非阻塞)    
    
* [网络IO的介绍](#network)

* [nio的概述](#description)

* [channel通道](#channel)

* [FileChannel文件IO](#fileChannel)

* [buffer缓冲区](#buffer)

* [selector选择器](#selector)

* [selectionKey](#selectionKey)

* [NIO的入门案例](#example)

* [网络聊天案例](#chat)

* [源码分析](#source)

* [IO 对比总结](#iod)

#### 网络IO
<div id="network"></div>
讲网络IO前，我们先对同步、异步、阻塞、非阻塞

#### 同步与异步
同步和异步关注的是消息通信机制 (synchronous communication/ asynchronous communication)。所谓同步，就是在发出一个*调用*时，在没有得到结果之前，该*调用*就不返回。但是一旦调用返回，就得到返回值了。换句话说，就是由*调用者*主动等待这个*调用*的结果。而异步则是相反，*调用*在发出之后，这个调用就直接返回了，所以没有返回结果。换句话说，当一个异步过程调用发出后，调用者不会立刻得到结果。而是在*调用*发出后，*被调用者*通过状态、通知来通知调用者，或通过回调函数处理这个调用。

典型的异步编程模型比如Node.js

举个通俗的例子：你打电话问书店老板有没有《分布式系统》这本书，如果是同步通信机制，书店老板会说，你稍等，"我查一下"，然后开始查啊查，等查好了（可能是5秒，也可能是一天）告诉你结果（返回结果）。而异步通信机制，书店老板直接告诉你我查一下啊，查好了打电话给你，然后直接挂电话了（不返回结果）。然后查好了，他会主动打电话给你。在这里老板通过"回电"这种方式来回调。

> 同步/异步是属于操作系统级别的，指的是操作系统在收到程序请求的IO之后，如果IO资源没有准备好的话，该如何响应程序的问题，同步的话就是不响应，直到IO资源准备好；而异步的话则会返回给程序一个标志，这个标志用于当IO资源准备好后通过事件机制发送的内容应该发到什么地方。
  
#### 阻塞与非阻塞
阻塞和非阻塞关注的是程序在等待调用结果（消息，返回值）时的状态.阻塞调用是指调用结果返回之前，当前线程会被挂起。调用线程只有在得到结果之后才会返回。非阻塞调用指在不能立刻得到结果之前，该调用不会阻塞当前线程。

还是上面的例子，你打电话问书店老板有没有《分布式系统》这本书，你如果是阻塞式调用，你会一直把自己"挂起"，直到得到这本书有没有的结果，如果是非阻塞式调用，你不管老板有没有告诉你，你自己先一边去玩了， 当然你也要偶尔过几分钟check一下老板有没有返回结果。在这里阻塞与非阻塞与是否同步异步无关。跟老板通过什么方式回答你结果无关。

> 阻塞/非阻塞是属于程序级别的，指的是程序在请求操作系统进行IO操作时，如果IO资源没有准备好的话，程序该怎么处理的问题，阻塞的话就是程序什么都不做，一直等到IO资源准备好，非阻塞的话程序则继续运行，但是会时不时的去查看下IO到底准备好没有呢

#### 网络IO分为五大模型
出自《UNIX网络编程》，I/O模型一共有阻塞式I/O，非阻塞式I/O，I/O复用(select/poll/epoll)，信号驱动式I/O和异步I/O。这篇文章讲的是I/O复用。
- blocking IO - 阻塞IO
- nonblocking IO - 非阻塞IO
- IO multiplexing - IO多路复用
- signal driven IO - 信号驱动IO
- asynchronous IO - 异步IO
#### nio的概述
<div id="description"></div>
 nio是jdk1.4提出的新特性，nio全名为non-blocking IO(非阻塞IO)
Java NIO 由以下几个核心部分组成： 
- Channels
- Buffers
- Selectors

<div id="channel"></div>

### 通道（Channel)

　通道：类似于流，但是可以异步读写数据（流只能同步读写），通道是双向的，（流是单向的），通道的数据总是要先读到一个buffer 或者 从一个buffer写入，即通道与buffer进行数据交互。这样的优点就是我们可以在读取的时候回退，对数据的操作更加灵活。

> 通道（Channel）：类似于BIO 中的stream，例如FileInputStream 对象，用来建立到目
  标（文件，网络套接字，硬件设备等）的一个连接，但是需要注意：BIO 中的stream 是单向
  的，例如FileInputStream 对象只能进行读取数据的操作，而NIO 中的通道(Channel)是双向的，
  既可以用来进行读操作，也可以用来进行写操作。常用的Channel 类有：FileChannel、
  DatagramChannel、ServerSocketChannel 和SocketChannel。FileChannel 用于文件的数据读写，
  DatagramChannel 用于UDP 的数据读写，ServerSocketChannel 和SocketChannel 用于TCP 的
  数据读写。

![image.png](https://upload-images.jianshu.io/upload_images/15181329-f2362ff3bd834e7c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
  
![image.png](https://upload-images.jianshu.io/upload_images/15204062-9b83f73b613429dc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

通道类型：
- FileChannel：从文件中读写数据。　　
- DatagramChannel：能通过UDP读写网络中的数据。　　
- SocketChannel：能通过TCP读写网络中的数据。　　
- ServerSocketChannel：可以监听新进来的TCP连接，像Web服务器那样。对每一个新进来的连接都会创建一个SocketChannel。　　
- FileChannel比较特殊，它可以与通道进行数据交互， 不能切换到非阻塞模式，套接字通道可以切换到非阻塞模式；

<div id="fileChannel"></div>

#### FileChannel

* public int read(ByteBuffer dst), 从通道读取数据并放到缓冲区中

* public int write(ByteBuffer src), 从通道读取数据写到缓冲区中

* public long transferTo(long position, long count, WritableByteChannel target)，把数据从当前通道复制给目标通道

* public long transferFrom(ReadableByteChannel src, long position, long count), 从目标通道中复制数据到当前通道


**案例1:往本地文件中写数据**

```java
    @Test
    public void test1() throws Exception {
        String str = "你好!,我是谁谁谁";
        FileOutputStream fos = new FileOutputStream("basic.txt");
        FileChannel fc = fos.getChannel(); //得到一个FileChannel对象
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);//创建一个大小为1024的缓冲区
        byteBuffer.put(str.getBytes()); //往缓冲区中写入数据
        byteBuffer.flip(); //翻转缓冲区，重置位置到初始位置
        fc.write(byteBuffer);//往FileChannel通道中写入数据
        fos.close();
    }
```
NIO 中的通道是从输出流对象里通过getChannel 方法获取到的，该通道是双向的，既可
以读，又可以写。在往通道里写数据之前，必须通过put 方法把数据存到ByteBuffer 中，然
后通过通道的write 方法写数据。在write 之前，需要调用flip 方法翻转缓冲区，把内部重置
到初始位置，这样在接下来写数据时才能把所有数据写到通道里

**案例2: 从本地文件中读数据**

```java
//从本地文件中读数据
    @Test
    public void test2() throws Exception{
        File file=new File("basic.txt");
        FileInputStream fis=new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        fc.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()).trim());
        fis.close();
        fc.close();

    }
```
上述代码从输入流中获得一个通道,然后提供ByteBuffer缓冲区,该缓冲区的大小和文件的大小一致,最后通过通道的read方法把数据读取出来并
存储到了ByteBuffer中.(例如客户端向服务端读数据,将数据读取到ByteBuffer中)

**案例3: nio读取文件**

```java

RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");  
FileChannel inChannel = aFile.getChannel();  
  
ByteBuffer buf = ByteBuffer.allocate(48);  
  
int bytesRead = inChannel.read(buf);  
while (bytesRead != -1) {  
  
System.out.println("Read " + bytesRead);  
buf.flip();  
  
while(buf.hasRemaining()){  
System.out.print((char) buf.get());  
}  
  
buf.clear();  
bytesRead = inChannel.read(buf);  
}  
aFile.close();  
```

#### ServerSocketChannel

用来在服务器端监听新的客户端Socket连接,常用方法:

* public static ServerSocketChannel open()，得到一个ServerSocketChannel 通道

* public final ServerSocketChannel bind(SocketAddress local)，设置服务器端端口号

* public final SelectableChannel configureBlocking(boolean block)，设置阻塞或非阻塞模式，取值false 表示采用非阻塞模式

* public SocketChannel accept()，接受一个连接，返回代表这个连接的通道对象

* public final SelectionKey register(Selector sel, int ops)，注册一个选择器并设置监听事件

#### SocketChannel

网络IO通道,具体负责进行读写操作,NIO总是把缓存区的数据写入到通道,或者把通道里的数据读到缓冲区,常用方法:

* public static SocketChannel open()，得到一个SocketChannel 通道

* public final SelectableChannel configureBlocking(boolean block)，设置阻塞或非阻塞模式，取值false 表示采用非阻塞模式

* public boolean connect(SocketAddress remote)，连接服务器

* public boolean finishConnect()，如果上面的方法连接失败，接下来就要通过该方法完成连接操作

* public int write(ByteBuffer src)，往通道里写数据

* public int read(ByteBuffer dst)，从通道里读数据
 
* public final SelectionKey register(Selector sel, int ops, Object att)，注册一个选择器并设置监听事件，最后一个参数可以设置共享数据
    
* public final void close()，关闭通道

<div id="buffer"></div>

### Buffer(缓冲区)

缓冲区 - 本质上是一块可以存储数据的内存，被封装成了buffer对象而已！

##### 1、缓冲区类型：

在NIO 中，Buffer 是一个顶层父类，它是一个抽象类，常用的Buffer 子类有：

- ByteBuffer:存储字节数据到缓冲区　
- CharBuffer:存储字符数据到缓冲区　　
- DoubleBuffer:存储小数到缓冲区　　
- FloatBuffer:存储小数到缓冲区　　
- IntBuffer:存储整数数据到缓冲区　　
- LongBuffer:存储长整型数据到缓冲区　　
- ShortBuffer: 存储字符串数据到缓冲区　
##### 2、常用方法：

对于Java 中的基本数据类型，都有一个Buffer 类型与之相对应，最常用的自然是
ByteBuffer 类（二进制数据），该类的主要方法如下所示

- public static ByteBuffer allocate(int capacity); - 设置缓冲区的初始容量　　
- public abstract ByteBuffer put(byte[] b); -  存储字节数据到缓冲区
- public abstract byte[] get(); - 从缓冲区获得字节数据　　
- public final Buffer flip(); - 将缓冲区从写模式切换到读模式(翻转缓冲区，重置位置到初始位置)　　
- public final byte[] array(); - 把缓冲区数据转换成字节数组
- public static ByteBuffer wrap(byte[] array); - 把一个现成的数组放到缓冲区中使用
- clear() - 从读模式切换到写模式，不会清空数据，但后续写数据会覆盖原来的数据，即使有部分数据没有读，也会被遗忘；　　
- compact() - 从读数据切换到写模式，数据不会被清空，会将所有未读的数据copy到缓冲区头部，后续写数据不会覆盖，而是在这些数据之后写数据
- mark() - 对position做出标记，配合reset使用
- reset() - 将position置为标记值
　　　　
##### 3、缓冲区的一些属性：
- capacity - 缓冲区大小，无论是读模式还是写模式，此属性值不会变；
- position - 写数据时，position表示当前写的位置，每写一个数据，会向下移动一个数据单元，初始为0；最大为capacity - 1，切换到读模式时，position会被置为0，表示当前读的位置
- limit - 写模式下，limit 相当于capacity 表示最多可以写多少数据，切换到读模式时，limit 等于原先的position，表示最多可以读多少数据。

<div id="selector"></div>

### Selector(选择器)

选择器：相当于一个观察者，用来监听通道感兴趣的事件，一个选择器可以绑定多个通道。
> Selector(选择器)，能够检测多个注册的通道上是否有事件发生，如果有事件发生，便获
  取事件然后针对每个事件进行相应的处理。这样就可以只用一个单线程去管理多个通道，也
  就是管理多个连接。这样使得只有在连接真正有读写事件发生时，才会调用函数来进行读写，
  就大大地减少了系统开销，并且不必为每个连接都创建一个线程，不用去维护多个线程，并
  且避免了多线程之间的上下文切换导致的开销。
  
![image.png](https://upload-images.jianshu.io/upload_images/15204062-cee15bf85fd22d79.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

该类常用的方法:

* public static Selector open(),得到一个选择器对象

* public int select(long timeout) 监听所有注册的通道,当其中有IO操作可以进行时,将对应的SelectionKey 加入到内部集合中并返回,参数用来设置超时时间

* public Set<SelectionKey>selectedKeys(),从内部集合中得到所有的SelectionKey

<div id="selectionKey"></div>

### SelectionKey

 SelectionKey代表了Selector和网络通道的注册关系,一共四种:

通道向选择器注册时，需要指定感兴趣的事件，选择器支持以下事件：
SelectionKey.OP_CONNECT
SelectionKey.OP_ACCEPT
SelectionKey.OP_READ
SelectionKey.OP_WRITE　　
如果你对不止一种事件感兴趣，那么可以用“位或”操作符将常量连接起来，如下：

int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;

要使用Selector，得向Selector注册Channel，然后调用它的select()方法。这个方法会一直阻塞到某个注册的通道有事件就绪。一旦这个方法返回，线程就可以处理这些事件，事件的例子有如新连接进来，数据接收等。

该类常用的方法:

* public abstract Selector selector(),得到与之关联的Selector对象 

* public abstract SelectableChannel channel()，得到与之关联的通道

* public final Object attachment()，得到与之关联的共享数据

* public abstract SelectionKey interestOps(int ops)，设置或改变监听事件

* public final boolean isAcceptable()，是否可以accept

* public final boolean isReadable()，是否可以读

* public final boolean isWritable()，是否可以写


<div id="example"></div>

### NIO的入门案例

**服务端**

```java
 public class NIOServer02 {
 
     public static void main(String[] args) throws IOException {
         //得到一个ServerSocketChanel对象
         ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
         //得到一个selector对象
         Selector selector = Selector.open();
         //绑定一个端口号
         serverSocketChannel.bind(new InetSocketAddress(9999));
         //设置非阻塞方式
         serverSocketChannel.configureBlocking(false);
         //把ServerSocketChannel对象注册给Selector对象
         serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
         while (true) {
             //监控客户端
             if (selector.select(2000) == 0) {
                 System.out.println("Server:没有客户端搭理我,我就干点别的事");
                 continue;
             }
             //得到SelectionKey,判断通道里的事件
             Set<SelectionKey> selectionKeys = selector.selectedKeys();
             Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
             while (keyIterator.hasNext()) {
                 SelectionKey key = keyIterator.next();
                 if (key.isAcceptable()) {//客户端连接请求
                     System.out.println("OP_ACCEPT");
                     SocketChannel socketChannel = serverSocketChannel.accept();
                     socketChannel.configureBlocking(false);
                     socketChannel.register(selector, SelectionKey.OP_READ,ByteBuffer.allocate(1024));
                 }
                 if (key.isReadable()) {//读取客户端数据事件
                     SocketChannel socketChannel = (SocketChannel) key.channel();//得到与之关联的通道
                     ByteBuffer byteBuffer = (ByteBuffer) key.attachment();//得到与之关联的共享数据
                     socketChannel.read(byteBuffer);//从通道里读数据
                     System.out.println("客户端发来数据:"+new String(byteBuffer.array()).trim());
                     byteBuffer.clear();
                 }
                 //手动从集合中移除当前 key,防止重复处理
                 keyIterator.remove();
             }
         }
 
     }
 }
```
**客户端**

```java
public class NIOClient02 {
    public static void main(String[] args) throws IOException {
        //得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞方式
        socketChannel.configureBlocking(false);
        //提供服务器的IP地址和端口号
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9999);
        //连接服务器端

        if (!socketChannel.connect(address)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("Client: 连接服务端的同时,我还可以干别的事情");
            }
        }
        //得到一个缓冲区并存入数据
        String msg = "hello Server";
        ByteBuffer writeBuf = ByteBuffer.wrap(msg.getBytes());
        //发送数据
        socketChannel.write(writeBuf);
        System.in.read();
    }
}
```
<div id="chat"></div>

### 网络聊天案例

**服务端代码:**

<a href="https://github.com/haoxiaoyong1014/recording/blob/master/src/main/java/cn/haoxiaoyong/record/nio/NIOChatServer.java">NIOChatServer</a>

**客户端代码**

<a href="https://github.com/haoxiaoyong1014/recording/blob/master/src/main/java/cn/haoxiaoyong/record/nio/NIOChatClient.java">NIOChatClient</a>

**启动客户端代码**

<a href="https://github.com/haoxiaoyong1014/recording/blob/master/src/test/java/cn/haoxiaoyong/record/nio/TestChat.java">TestChat</a>

<div id="source"></div>

### 源码分析

<a href="https://juejin.im/post/5c2e23156fb9a049ff4e4009">BIO到NIO源码的一些事儿之NIO 上</a>

<a href="https://juejin.im/post/5c34d1dd6fb9a049c84fa2ce">BIO到NIO源码的一些事儿之NIO 中</a>

<div id="aio"></div>

### AIO 编程

JDK 7 引入了Asynchronous I/O，即AIO。在进行I/O 编程中，常用到两种模式：Reactor
和Proactor。Java 的NIO 就是Reactor，当有事件触发时，服务器端得到通知，进行相应的
处理。
AIO 即NIO2.0，叫做异步不阻塞的IO。AIO 引入异步通道的概念，采用了Proactor 模式，
简化了程序编写，一个有效的请求才启动一个线程，它的特点是先由操作系统完成后才通知
服务端程序启动线程去处理，一般适用于连接数较多且连接时间较长的应用。目前AIO 还没有广泛应用,不作为重点.

<div id="iod"></div>

### IO 对比总结

IO 的方式通常分为几种：同步阻塞的BIO、同步非阻塞的NIO、异步非阻塞的AIO。

* BIO 方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4 以前的唯一选择，但程序直观简单易理解。

* NIO 方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，并发局限于应用中，编程比较复杂，JDK1.4 开始支持。
 
* AIO 方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用OS 参与并发操作，编程比较复杂，JDK7 开始支持。
    
举个例子再理解一下：

* 同步阻塞:  你到饭馆点餐，然后在那等着，啥都干不了，饭馆没做好，你就必须等着！

* 同步非阻塞：你在饭馆点完餐，就去玩儿了。不过玩一会儿，就回饭馆问一声：好了没啊！

* 异步非阻塞：饭馆打电话说，我们知道您的位置，一会给你送过来，安心玩儿就可以了，类似于现在的外卖。
  

  | 对比总结 | BIO | NIO | AIO
  | :-------| :----|:----|:----|
  | IO方式 | 同步阻塞| 同步非阻塞(多路复用) | 异步非阻塞 |
  | API使用难度 | 简单 | 复杂 | 复杂 |
  | 可靠性 | 差 | 好 | 好 |
  | 吞吐量 | 低 | 高 | 高|
   