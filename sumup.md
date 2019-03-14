#### **同步异步:**

![image.png](https://upload-images.jianshu.io/upload_images/15181329-49cd4d976df8a2a8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

同步:线程A请求资源,等待资源就绪获取结果,

#### **阻塞非阻塞:**
![image.png](https://upload-images.jianshu.io/upload_images/15181329-185331d1cbea83bd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

阻塞:线程A会等待资源的就绪在去执行其他的操作
#### BIO

阻塞IO:

![image.png](https://upload-images.jianshu.io/upload_images/15181329-f2c67486a6955456.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
IO在读写过程中是会阻塞的,并发处理能力非常的低,而且耗时是非常的长,jdk1.4之前都是这样
每一个客户端过来都会创建一个线程,典型的一问一答的形式,
频繁的创建和频繁的销毁线程,创建个线程池(伪异步)

#### NIO
非阻塞IO:
![image.png](https://upload-images.jianshu.io/upload_images/15181329-1de9e8be9528c23c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**selector**(选择器或多路复用器),他会主动的轮询是否有客户端过来,如果有,每一个客户端都会注册到selector上,注册完毕之后就会有一个**channel**,这个channel是一个双向通道,进行相应的数据读写,这些数据的读写都会在我们缓冲区**Buffer**里,也就是说数据的读写是通过Buffer完成,
selector是一个单线程的,客户端的增多不会影响他的性能,

#### AIO
![image.png](https://upload-images.jianshu.io/upload_images/15181329-15bff1e40acd612e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### 总结
**同步阻塞: BIO** 

例如:你现在去上厕所,然而这个时候测试的位置是满的,你就一直等着什么事情都不做,并主动的观察有没有位置.

**同步非阻塞: NIO**

例如: 同样你还是要去厕所,这个位置也是满的,但是呢这个时候,我一边等一边玩手机或者抽烟.但是我时不时的要去观察一下有没有位置.

**异步阻塞: SIO**

在开发中几乎是用不到的,因为这种方式有点傻,
例如:我们去上厕所,这个时候位置也是全满的,这个时候我站在厕所里什么也不干,只是光等着,然后让每一个坑的用户释放完之后让他来告诉我,你可以过去上厕所了,有点鸡肋,

**异步非阻塞: AIO(NIO2.0)**

例如: 当然我还是去上厕所,这个时候位置很遗憾也是满的,我这个时候我去外边抽根烟,玩会手机,如果有用户释放完了,这个他会过来告诉我,嗨!哥们,我释放完了,你可以去释放一下了.