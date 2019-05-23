### Bio(同步阻塞)

[到底什么是“IO Block”](#到底什么是“io-block”)

[BIO介绍](#BIO介绍)

[BIO代码实例](#BIO代码实例)

[多线程的方式-同步阻塞式I/O](#thread)

[利用线程池解决BIO-伪异步I/O模型](#threadPool)

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

<div id="thread"></div>

#### 多线程的方式-同步阻塞式I/O

当然这种方式并不是我们想要的方式,因为阻塞的原因,上面我们也提到了用线程的方式去解决这样的问题,每个请求分配一个线程,但这不是最终的解决方法,

下面我们看下用多线程的方式解决:

**服务端: BioThreadServer**

```java
public class BioThreadServer {

    public static void start() throws IOException {
// 1,创建ServerSocket对象
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(9998);
        while (true) {
            System.out.println("没有客户端连接,我阻塞在这里了.....");
            //2,监听客户端
            Socket accept = serverSocket.accept();//阻塞,
            Thread thread = new Thread(new BioServerThreadHandler(accept));//.start();
            thread.start();
            System.out.println(thread.getName());

        }
    }
 } 
 
```
**BioServerThreadHandler**

```java
public class BioServerThreadHandler implements Runnable {

    private Socket socket;

    public BioServerThreadHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //3,从连接中取出输入流来接收消息
        InputStream inputStream = null;
        try {
           // while (true){
                System.out.println("有客户端连接了...");
                inputStream = socket.getInputStream();//阻塞
                System.out.println("但是我没有收到客户端发来的消息,我又阻塞在这里了.....");
                byte[] by = new byte[10];
                inputStream.read(by);
                System.out.println("我收到客户端的消息了...");

                String hostAddress = socket.getInetAddress().getHostAddress();
                System.out.println(hostAddress + "说:" + new String(by).trim());
                //4.从连接中取出输出流并回话
                OutputStream outputStream = socket.getOutputStream();//(4)
                outputStream.write("没钱".getBytes());
           // }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

```
**客户端: BioThreadClient**

```java
public class BioThreadClient {

    public static void send() {
        while (true) {
            //1.创建Socket 对象
            Socket socket = null;
            try {
                socket = new Socket("127.0.0.1", 9998);
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
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
```
**服务端测试方法**

```java

public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BioThreadServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
```

**客户端测试方法**

```java
public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BioThreadClient.send();
            }
        }).start();
    }

```
**测试结果:**

![5411548743272_.pic_hd.jpg](https://upload-images.jianshu.io/upload_images/15181329-599f6bcb4769317b.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![5421548743483_.pic.jpg](https://upload-images.jianshu.io/upload_images/15181329-e800effd9936175f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![5431548743483_.pic.jpg](https://upload-images.jianshu.io/upload_images/15181329-78bca56907c8ed1c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![5441548743483_.pic.jpg](https://upload-images.jianshu.io/upload_images/15181329-981f0c9934ed37d3.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这种方式用图形说明(<a href="https://blog.csdn.net/anxpp/article/details/51512200">图片来源于网络</a>):

![5451548745181_.pic.jpg](https://upload-images.jianshu.io/upload_images/15181329-628a2a755d51920c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

以为这样就结束了吗?当然不是,频繁的创建和销毁线程是极其浪费资源的,他们消耗着CPU和内存,线程池可以根据在创建线程池时选择的策略自动处理线程生命周期。线程池的一个重要特性是它允许应用程序优雅地降级。
线程池中的线程是可以重复的使用,当一个线程结束之后放回线程池,下次有请求我们再从线程池中拿取,下面我们就用线程池来解决上面的问题(虽然不是从根本上解决问题)

<div id="threadPool"></div>

#### 利用线程池解决BIO-伪异步I/O模型

实现很简单，我们只需要将新建线程的地方，交给线程池管理即可，只需要改动刚刚的BioThreadServer代码即可：

```java
class BioThreadPoolServer {

    //创建一个核心线程数和最大线程数都为5的线程池
    private static ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    //private static ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    public static void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(9998);
        while (true) {
            System.out.println("没有客户端连接,我阻塞在这里了.....");
            //监听客户端
            Socket accept = serverSocket.accept();
            executorService.execute(new BioServerThreadHandler(accept));
            System.out.println("核心线程数: "+executorService.getCorePoolSize());
            System.out.println("最大线程数: "+executorService.getMaximumPoolSize());
        }
    }
}
```
测试运行结果是一样的。

这种方式用图形说明(<a href="https://blog.csdn.net/anxpp/article/details/51512200">图片来源于网络</a>):

![image.png](https://upload-images.jianshu.io/upload_images/15181329-e360e4d254ae1421.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我们知道，如果使用CachedThreadPool线程池（不限制线程数量），其实除了能自动帮我们管理线程（复用），看起来也就像是1:1的客户端：线程数模型，
而使用FixedThreadPool我们就有效的控制了线程的最大数量，保证了系统有限的资源的控制，实现了N:M的伪异步I/O模型。

但是，正因为限制了线程数量，如果发生大量并发请求，超过最大数量的线程就只能等待，直到线程池中的有空闲的线程可以被复用。而对Socket的输入流就行读取时，会一直阻塞，直到发生：

            有数据可读
    
            可用数据以及读取完毕
    
            发生空指针或I/O异常


所以在读取数据较慢时（比如数据量大、网络传输慢等），大量并发的情况下，其他接入的消息，只能一直等待，这就是最大的弊端。
当然上面也说到了,这没有从根本上去解决问题,而后面即将介绍的<a href="https://github.com/haoxiaoyong1014/recording/blob/master/nio.md">NIO</a>，
就能解决这个难题。

<a href="https://juejin.im/post/5c2cc075f265da611037298e">BIO到NIO源码的一些事儿之BIO</a>








