### Bio

[到底什么是“IO Block”](#到底什么是“io-block”)

[BIO介绍](#BIO介绍)

[BIO代码实例](#BIO代码实例)

#### 到底什么是“IO Block”

BIO 有的称之为basic(基本) IO,有的称之为block(阻塞) IO，主要应用于文件IO 和网络IO，
这里不再说文件IO, 大家对此都非常熟悉，本次课程主要讲解网络IO。
在JDK1.4 之前，我们建立网络连接的时候只能采用BIO，需要先在服务端启动一个
ServerSocket，然后在客户端启动Socket 来对服务端进行通信，默认情况下服务端需要对每
个请求建立一个线程等待请求，而客户端发送请求后，先咨询服务端是否有线程响应，如果
没有则会一直等待或者遭到拒绝，如果有的话，客户端线程会等待请求结束后才继续执行，
这就是阻塞式IO。

很多人多BIO不好,会block,那到底什么是`IO Block`呢? 下面思考两个问题:

       - 用系统调用read从socket中读取一段数据
       - 用系统调用read从一个磁盘文件读取一段数据到内存
如果你的直觉告诉你，这两种都算“Block”，那么很遗憾，你的理解与Linux不同。Linux认为：
    
       - 对于第一种情况，算作block，因为Linux无法知道网络上对方是否会发数据。如果没数据发过来，对于调用read的程序来说，就只能“等”。
       - 对于第二种情况，不算做block。
对于磁盘文件 io,linux不视作是block

你可能会说，这不科学啊，磁盘读写偶尔也会因为硬件而卡壳啊，怎么能不算Block呢？但实际就是不算。

> 一个解释是，所谓“Block”是指操作系统可以预见这个Block会发生才会主动Block。例如当读取TCP连接的数据时，如果发现Socket buffer里没有数据就可以确定定对方还没有发过来，
于是Block；而对于普通磁盘文件的读写，也许磁盘运作期间会抖动，会短暂暂停，但是操作系统无法预见这种情况，只能视作不会Block，照样执行。
           
#### BIO介绍           
           
 有了Block的定义，就可以讨论BIO和NIO了。BIO是Blocking IO的意思。在类似于网络中进行read, write, connect一类的系统调用时会被卡住。
 
 举个例子，当用read去读取网络的数据时，是无法预知对方是否已经发送数据的。因此在收到数据之前，能做的只有等待，直到对方把数据发过来，或者等到网络超时。
 对于单线程的网络服务，这样做就会有卡死的问题。因为当等待时，整个线程会被挂起，无法执行，也无法做其他的工作。
 
 > 顺便说一句，这种Block是不会影响同时运行的其他程序（进程）的，因为现代操作系统都是多任务的，任务之间的切换是抢占式的。这里Block只是指Block当前的进程。
 
 于是，网络服务为了同时响应多个并发的网络请求，必须实现为多线程的。每个线程处理一个网络请求。线程数随着并发连接数线性增长。这的确能奏效。实际上2000年之前很多网络服务器就是这么实现的。但这带来两个问题：
 
    - 线程越多，Context Switch就越多，而Context Switch是一个比较重的操作，会无谓浪费大量的CPU。
    - 每个线程会占用一定的内存作为线程的栈。比如有1000个线程同时运行，每个占用1MB内存，就占用了1个G的内存
 > 也许现在看来1GB内存不算什么，现在服务器上百G内存的配置现在司空见惯了。但是倒退20年，1G内存是很金贵的。并且，尽管现在通过使用大内存，可以轻易实现并发1万甚至10万的连接。但是水涨船高，如果是要单机撑1千万的连接呢？
 
 问题的关键在于，当调用read接受网络请求时，有数据到了就用，没数据到时，实际上是可以干别的。使用大量线程，仅仅是因为Block发生，没有其他办法。
 
 当然你可能会说，是不是可以弄个线程池呢？这样既能并发的处理请求，又不会产生大量线程。但这样会限制最大并发的连接数。比如你弄4个线程，那么最大4个线程都Block了就没法响应更多请求了。   
      

#### BIO代码实例
    
**BIO 服务端程序**

```java
public class TCPServer {

    public static void main(String[] args) throws Exception {
        // 1,创建ServerSocket对象
        ServerSocket serverSocket = new ServerSocket(9999);
        while (true) {

            System.out.println("没有客户端连接,我阻塞在这里了.....");
            //2,监听客户端
            Socket accept = serverSocket.accept();//阻塞,
            System.out.println("有客户端连接了...");
            //3,从连接中取出输入流来接收消息
            InputStream inputStream = accept.getInputStream();//阻塞
            System.out.println("但是我没有收到客户端发来的消息,我又阻塞在这里了.....");
            byte[] by = new byte[10];
            inputStream.read(by);
            System.out.println("我收到客户端的消息了...");
            String hostAddress = accept.getInetAddress().getHostAddress();
            System.out.println(hostAddress + "说:" + new String(by).trim());
            //4.从连接中取出输出流并回话
            OutputStream outputStream = accept.getOutputStream();//(4)
            outputStream.write("没钱".getBytes());
            accept.close();
        }
    }
}
``` 
   
**BIO 客户端程序** 

```java
public class TCPClient {

    public static void main(String[] args) throws Exception {
        while (true) {
            //1.创建Socket 对象
            Socket socket = new Socket("127.0.0.1", 9999);
            //2.从连接中取出输出流并发消息
            OutputStream outputStream = socket.getOutputStream();
            System.out.println("请输入:");
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine();
            outputStream.write(msg.getBytes());
            System.out.println("我没有收到服务器的消息,我阻塞在这里了...");
            //3.从连接中取出输入流并接收回话
            InputStream inputStream = socket.getInputStream();//阻塞
            byte[] b = new byte[20];
            inputStream.read(b);
            System.out.println("我收到服务器的消息了");
            System.out.println("老板:" + new String(b).trim());
            socket.close();
        }
    }
}

```  
**测试结果**

![image.png](https://upload-images.jianshu.io/upload_images/15181329-934f22e2592d63ef.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](https://upload-images.jianshu.io/upload_images/15181329-fef4b73c982bcc82.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)