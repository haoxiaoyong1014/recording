<div id="top"></div>

### 多线程编程

* [基本知识回顾](#mark)

* [怎么理解多线程](#lijie)

* [两种方式的区别](#qubie)

* [获取当前线程的对象](#pojo)

* [守护线程](#protect)

* [加入线程(join(),join(int))](#join)

* [线程安全](#anquan)

* [出现线程安全的原因](#yuanyin)

* [解决线程安全问题](#jieju)

* [死锁](#lock)

* [java API 中的线程安全问题](#API)

* [线程间的通信](#tongxin)

* [线程的生命周期](#live)

* [生产者消费者模式](#sq)

* [生产者-消费者与队列](#queue)

* [synchronized底层语义原理](#synchronized底层语义原理)

<div id="Mark"></div>

#### 基本知识回顾 

线程是比进程更小的能独立运行的基本单位,他是进程的一部分,一个进程可以有多个进程,但至少有一个线程,即主线程执行(java的 main方法).
我们既可以编写单线程应用也可以编写多线程樱应用.

一个进程中的多个线程可以并发(同步)的去执行,在一些执行时间长,需要等待的任务上(例如:文件读写和网络传输等),多线程就比较有用了.

<div id="lijie"></div>

**怎么理解多线程?**

1.进程就是一个工厂,一个线程就是工厂中的一条生产线,一个工厂至少有一条生产线,只有一条生产线就是单线程应用,拥有多条生产线就是多线程应用
多条生产线可以同时运行.

2.我们使用迅雷可以同时下载多个视频，迅雷就是进程，多个下载任务就是线程，这几个线程可以同时运行去下载视频
  
多线程可以共享内存,充分利用CPU,通过提高资源(内存和CPU)使用率从而提高程序的执行效率.CPU使用抢占式调度模式在多个线程间随机高速的切换,
对于CPU的一个核而言,某个时刻,只执行一个线程,而CPU在多个线程间的切换速度相对我们的感觉要快很多,看上去就像是多个线程或任务在同时运行

java 天生就支持多线程并提供了两种编程方式,一种是继承Thread和实现Runnable接口:

**ThreadFor1**

```java
public class ThreadFor1 extends Thread {

    public void run() {
        for (int i = 0; i < 50; i++) {
            System.out.println(this.getName() + ":" + i);
        }
    }
}
```
**ThreadFor2**

```java
public class ThreadFor2 extends Thread {

    public void run() {
        for (int i = 51; i < 100; i++) {
            System.out.println(this.getName() + ":" + i);
        }
    }
}
```
**测试类**

```
    @Test
    public void testThread() {
        ThreadFor1 for1 = new ThreadFor1();
        for1.setName("线程A");
        ThreadFor2 for2=new ThreadFor2();
        for2.setName("线程B");
        for1.start();
        for2.start();
    }
```
**测试结果**
```
线程A:0
线程B:51
线程A:1
线程B:52
线程B:53
线程A:2
线程B:54
线程A:3
线程B:55
线程A:4
线程B:56
线程A:5
线程B:57
线程A:6
线程B:58
```

通过继承Thread类,ThreadFor1和ThreadFor2就是线程类,通过测试发现CPU在两个线程之间快速随机切换,也就是我们所说的同时执行


**RunnableFor1**
```java
public class RunnableFor1 implements Runnable {

    public void run() {
        for (int j = 0; j < 50; j++) {
            System.out.println(Thread.currentThread().getName() + ":" + j);
        }
    }
}
```
**RunnableFor2**
```java
public class RunnableFor2 implements Runnable {

    public void run() {
        for (int i = 51; i < 100; i++) {
            System.out.println(Thread.currentThread().getName()+":"+i);
        }
    }
}
```
**测试类**

```
@Test
    public void testRunnable() {
        Thread t1 = new Thread(new RunnableFor1());
        t1.setName("线程A");
        Thread t2 = new Thread(new RunnableFor2());
        t2.setName("线程B");
        t1.start();
        t2.start();
    }
```

**测试结果**
```
同上
```
RunnableFor1和RunnableFor2这个时候是通过实现Runnable类来实现的,这个时候RunnableFor1和RunnableFor2就不能叫线程类了,可以叫任务类

<div id="qubie"></div>

#### 两种方式的区别

查看源码我们发现: 
    
    继承Thread: 由于子类重写了Thread类的run(),当调用 statrt()时,直接找子类的run()方法
    
    实现Runnable: 构造函数中传入Runnable的引用,成员变量记住了它,statrt()调用run()方法时内部判断成员变量Runable的引用是否
    为空,不为空编译看的是Runnable的run(),运行是执行的是子类的run()方法.
    
继承Thread:
    
    好处: 可以直接使用Thread类中方法,代码简单    
    
    弊端: 如果已经了父类,就不能用这种方法
    
实现Runnable接口:

    好处: 即使自己定义了线程类也没有什么关系,因为有了父类也可以实现接口,而且接口是可以多实现,
    
    弊端: 不能直接使用Thread中的方法需要先获取到线程对象后,才能得到Thread的方法,代码复杂   

个人理解: 

    实现Runnable接口是对继承Thread类的一种补充.
    
<div id="pojo"></div>

#### 获取当前线程的对象

**Thread.currentThread()**

```java
 public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " aaaaaaaaaa");
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " bb");
            }
        }).start();
        Thread.currentThread().setName("我是主线程");                    //获取主函数线程的引用,并改名字
        System.out.println(Thread.currentThread().getName());        //获取主函数线程的引用,并获取名字
    }    
```

<div id="protect"></div>

#### 守护线程

**setDaemon()设置一个线程为守护线程, 该线程不会单独执行, 当其他非守护线程都执行结束后, 自动退出**

```java
public static void main(String[] args) {

        Thread t1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    System.out.println(getName() + " ...aaaaaaaa");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(getName()+ " ...bb");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.setDaemon(true);             //将t1设置为守护线程
        t1.start();
        t2.start();
    }
```
此例子是将t1设置为守护线程

**打印结果**

```
Thread-0 ...aaaaaaaa
Thread-1 ...bb
Thread-1 ...bb
Thread-0 ...aaaaaaaa
Thread-0 ...aaaaaaaa
Thread-1 ...bb
Thread-1 ...bb
Thread-0 ...aaaaaaaa
Thread-1 ...bb
Thread-0 ...aaaaaaaa
Thread-0 ...aaaaaaaa
```
当t2执行完毕之后 t1自动退出, 守护线程起到的就是监控,管理的作用.

举个例子:

    就像 城堡门前有个卫兵 （守护线程），里面有诸侯（非守护线程），他们是可以同时干着各自的活儿，但是 城堡里面的人都搬走了， 那么卫兵也就没有存在的意义了。

> 用户线程即运行在前台的线程，而守护线程是运行在后台的线程。 守护线程作用是为其他前台线程的运行提供便利服务，
而且仅在普通、非守护线程仍然运行时才需要，比如垃圾回收线程就是一个守护线程。

另外有几点需要注意:
    
    1,setDaemon(true)必须在调用线程的start（）方法之前设置，否则会抛出IllegalThreadStateException异常
    
    2,在守护线程中产生的新线程也是守护线程。
    
    3,不要在守护线程中执行业务逻辑操作
    
<div id="join"></div>

#### 加入线程(join(),join(int))

join(), 当前线程暂停, 等待指定的线程执行结束后, 当前线程再继续
join(int), 可以等待指定的毫秒之后继续

```java
public static void main(String[] args) {
        final Thread t1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    System.out.println(getName() + " ....aaaaa");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    if (i == 2) {
                        try {
                            //t1.join();              //插队,加入
                            t1.join(30);
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(getName() + " ....bbb");
                }
            }
        };
        t1.start();
        t2.start();
    }
```

<div id="anquan"></div>

#### 线程安全 

**产生多线程安全问题的原因**
 
在进行多线程编程时,要注意线程安全问题,
 
**SaleWindow** 
 ```java
public class SaleWindow implements Runnable {

    private int id = 10; //表示10张火车票

    public void run() {

        for (int i = 0; i < 10; i++) {
            if (id > 0) {
                System.out.println(Thread.currentThread().getName() + "卖了编号为" + id + "的火车票");
                id--;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```
**测试类**

```
public static void main(String[] args) {
        SaleWindow saleWindow = new SaleWindow();
        Thread t1 = new Thread(saleWindow);
        Thread t2 = new Thread(saleWindow);
        t1.setName("窗口 A");
        t2.setName("窗口 B");
        t1.start();
        t2.start();
    }
```
**测试结果:**

```
窗口 A卖了编号为10的火车票
窗口 B卖了编号为9的火车票
窗口 A卖了编号为8的火车票
窗口 B卖了编号为8的火车票
窗口 A卖了编号为6的火车票
窗口 B卖了编号为5的火车票
窗口 A卖了编号为4的火车票
窗口 B卖了编号为4的火车票
窗口 A卖了编号为2的火车票
窗口 B卖了编号为1的火车票
```

我们看到，10 张火车票都卖出去了，但是出现了重复售票，这就是线程安全问题造成
的。这10 章火车票是共享资源，也就是说任何窗口都可以进行操作和销售，问题在于窗口
A 把某一张火车票卖出去之后，窗口B 并不知道，因为这是两个线程，所以窗口B 也可能会
再卖出去一张相同的火车票。

<div id="yuanyin"></div>

#### 线程安全问题出现的原因 

要说明线程同步问题首先要说明Java线程的两个特性，可见性和有序性。多个线程之间是不能直接传递数据交互的，它们之间的交互只能通过共享变量来实现,
就拿这个例子来说,两个线程共享了同一个id变量,这个对象被创建在主内存(堆内存)中,每个线程都有自己的工作内存(线程栈),工作内存存储了主内存id对象
的一个副本,当线程操作 id对象时,首先从主内存中复制 id对象的一个副本到工作内存中,然后执行 `id--`,改变了id的值,最后工作内存id刷新主内存id,
当一个对象在多个内存中都存在副本时,如果一个内存修改了工作变量,其他线程也应该能看到这个修改后的值,这个就叫**可见性**,
多个线程执行时，CPU对线程的调度是随机的，我们不知道当前程序被执行到哪步就切换到了下一个线程，一个最经典的例子就是银行汇款问题，
一个银行账户存款100，这时一个人从该账户取10元，同时另一个人向该账户汇10元，那么余额应该还是100。那么此时可能发生这种情况，
A线程负责取款，B线程负责汇款，A从主内存读到100，B从主内存读到100，A执行减10操作，并将数据刷新到主内存，这时主内存数据100-10=90，
而B内存执行加10操作，并将数据刷新到主内存，最后主内存数据100+10=110，显然这是一个严重的问题，我们要保证A线程和B线程有序执行，先取款后汇款或者先汇款后取款，此为**有序性**。


<img src="https://upload-images.jianshu.io/upload_images/15181329-927d3143d10abb49.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240">

多个线程操作的是同一个共享资源，但是线程之间是彼此独立、互相隔绝的，因此就会
出现数据（共享资源）不能同步更新的情况，这就是线程安全问题。

<div id="jieju"></div>

#### 解决线程安全问题

java 中提供了一个同步机制(锁)来解决线程安全问题,即让操作共享数据的代码在某一时间段,只被一个线程执行(锁住),
在执行过程中,其他线程不可以参与进来,这样共享数据就能同步了,简单来说,就是给某些代码加把锁.
锁是什么? 又从哪里来?锁的专业名称叫监视器 monitor,其实 java 为每个对象都自带内置了一个锁(监视器 monitor),
当某个线程执行到了某代码快时就会自动得到这个对象的锁,那么其他线程就无法执行该代码块了,一直要等到之前那个线程停止(释放锁),
需要特别注意的是:多个线程必须使用同一把锁(对象).

* 同步代码块:即给代码块上锁,变成同步代码块

* 同步方法: 即给方法上锁,变成同步方法


**方式一:**

```java
public class SaleWindow1 implements Runnable {

    private int id = 10; //表示10张火车票

    public void run() {

        for (int i = 0; i < 10; i++) {
            synchronized (this){
                if (id > 0) {
                    System.out.println(Thread.currentThread().getName() + "卖了编号为" + id + "的火车票");
                    id--;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

```
**测试结果**

```
窗口 A卖了编号为10的火车票
窗口 A卖了编号为9的火车票
窗口 A卖了编号为8的火车票
窗口 A卖了编号为7的火车票
窗口 A卖了编号为6的火车票
窗口 A卖了编号为5的火车票
窗口 A卖了编号为4的火车票
窗口 A卖了编号为3的火车票
窗口 A卖了编号为2的火车票
窗口 A卖了编号为1的火车票
```

**方式二:**

```java
public class SaleWindow2 implements Runnable {

    private int id = 10; //表示10张火车票

    public void run() {

        for (int i = 0; i < 10; i++) {

            saleOne();
        }

    }

    private synchronized void saleOne() {
        if (id > 0) {
            System.out.println(Thread.currentThread().getName() + "卖了编号为" + id + "的火车票");
            id--;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

```
**测试结果**

```
窗口 A卖了编号为10的火车票
窗口 A卖了编号为9的火车票
窗口 A卖了编号为8的火车票
窗口 A卖了编号为7的火车票
窗口 A卖了编号为6的火车票
窗口 A卖了编号为5的火车票
窗口 B卖了编号为4的火车票
窗口 B卖了编号为3的火车票
窗口 B卖了编号为2的火车票
窗口 B卖了编号为1的火车票
```

第二种方式是把原来同步代码块中的代码抽取出来放到一个方法中，然后给这个方法加上
synchronized 关键字修饰，锁住的代码是一样的，因此本质上和第一种方式没什么区别。

synchronized的作用就是说进入这个带有synchronized关键字修饰的方法时,这个方法必须要执行完毕你才能离开,通俗的讲就是这块代码
被锁住了,必须执行完你才能执行其他的线程.使用synchronized修饰的方法或者代码块可以看成是一个**原子操作**。

每个锁对象(JLS中叫monitor)都有两个队列，一个是就绪队列，一个是阻塞队列，就绪队列存储了将要获得锁的线程，阻塞队列存储了被阻塞的线程，
当一个线程被唤醒(notify)后，才会进入到就绪队列，等待CPU的调度，反之，当一个线程被wait后，就会进入阻塞队列，等待下一次被唤醒，这个涉及到[线程间的通信](#tongxin),
看我们的例子，当第一个线程执行输出方法时，获得同步锁，执行输出方法，恰好此时第二个线程也要执行输出方法，但发现同步锁没有被释放，第二个线程就会进入就绪队列，等待锁被释放.

一个线程执行互斥代码过程如下：

```
1. 获得同步锁；

2. 清空工作内存；

3. 从主内存拷贝对象副本到工作内存；

4. 执行代码(计算或者输出等)；

5. 刷新主内存数据；

6. 释放同步锁。
```
所以，synchronized既保证了多线程的并发有序性，又保证了多线程的内存可见性。

<img src="https://upload-images.jianshu.io/upload_images/15181329-fc5b69aee4c114af.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240">

<div id="lock"></div>

#### 死锁

当某个任务在等待另一个任务,而后者又等待别的任务,这样一直下去,直到这个链条上的任务又在等待第一个任务释放锁,这得到了一个任务之间相互等待的连续循环,
没有哪个线程能继续,这就被称之为死锁.

多线程同步的时候, 如果同步代码嵌套, 使用相同锁, 就有可能出现死锁

尽量不要嵌套使用
		
```java
private static String s1 = "筷子左";
			private static String s2 = "筷子右";
			public static void main(String[] args) {
				new Thread() {
					public void run() {
						while(true) {
							synchronized(s1) {
								System.out.println(getName() + "...拿到" + s1 + "等待" + s2);
								synchronized(s2) {
									System.out.println(getName() + "...拿到" + s2 + "开吃");
								}
							}
						}
					}
				}.start();
				
				new Thread() {
					public void run() {
						while(true) {
							synchronized(s2) {
								System.out.println(getName() + "...拿到" + s2 + "等待" + s1);
								synchronized(s1) {
									System.out.println(getName() + "...拿到" + s1 + "开吃");
								}
							}
						}
					}
				}.start();
			}
```
**打印结果**

```
Thread-0...拿到筷子左等待筷子右
Thread-0...拿到筷子右开吃
Thread-0...拿到筷子左等待筷子右
Thread-0...拿到筷子右开吃
Thread-0...拿到筷子左等待筷子右
Thread-0...拿到筷子右开吃
Thread-0...拿到筷子左等待筷子右
Thread-0...拿到筷子右开吃
Thread-0...拿到筷子左等待筷子右
Thread-0...拿到筷子右开吃
Thread-0...拿到筷子左等待筷子右
Thread-0...拿到筷子右开吃
Thread-0...拿到筷子左等待筷子右
Thread-0...拿到筷子右开吃
Thread-0...拿到筷子左等待筷子右
Thread-1...拿到筷子右等待筷子左
```
要想解决死锁问题,你必须明白,当以下四个条件同时满足的时候就会发生死锁.

    1,互斥条件,任务中使用的资源至少有一个是不能共享的,这里,一根筷子一次就只能被一个哲学家使用,
    
    2,至少有一个任务他必须持有一个资源且这个资源正在等待获取一个当前被别的任务持有的资源,也就是说,要发生死锁,哲学家必须拿着一根筷子并且等待另一根
    
    3,资源不能被任务抢占,任务必须把资源释放当作普通的事件,哲学家很有礼貌,他们不会从其他哲学家那里抢筷子
    
    4,必须有循环等待,这时,一个任务等待其他任务所持有的资源,后者又在等待另一个任务所持有的资源,这样一直下去,直到有一个任务在等待第一个任务所持有的资源,使大家都被锁住,
    这里每个哲学家都试图先得到右边的筷子,然后得到左边的筷子,所以发生了循环等待
    
因为要发生死锁的话,所有这些条件都必须满足;所以要防止死锁的话,只需破坏其只一个即可,在这里防止死锁最容易的方式是破坏第四个条件,有这个条件的原因是每个哲学家都试图
用特定的顺序去拿筷子,先右后左;正因为如此,每个人都拿着右边的筷子,并等待左边的筷子,这就是循环等待的条件,如果最后一个哲学家被初始化成先从左边拿筷子,后拿右边的筷子
    
<div id="API"></div>

#### java API 中的线程安全问题

我们平时在使用Java API 进行编程时，经常遇到说哪个类是线程安全的，哪个类是不保
证线程安全的，例如：StringBuffer / StringBuilder 和Vector / ArrayList ，谁是线程安全的？
谁不是线程安全的？我们查一下它们的源码便可知晓。

**StringBuffer**
<img src="https://upload-images.jianshu.io/upload_images/15181329-afaa5eb8ee01ce11.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240">

**StringBuilder**
<img src="https://upload-images.jianshu.io/upload_images/15181329-991e7be0d66cf27a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240">

**Vector**

<img src="https://upload-images.jianshu.io/upload_images/15181329-9239de1b638ee1e6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240">

**ArrayList**

<img src="https://upload-images.jianshu.io/upload_images/15181329-074e15b032c37788.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240">

通过查看源码，我们发现StringBuffer 和Vector 类中的大部分方法都是同步方法，所以证明这两个类在使用时是保证线程安全的；而StringBuilder 和ArrayList 类中的方法都是普通方法，
没有使用synchronized 关键字进行修饰，所以证明这两个类在使用时不保证线程安全。线程安全和性能之间不可兼得，保证线程安全就会损失性能，保证性能就不能满足线程安全。
 
<div id="tongxin"></div> 
 
#### 线程间的通信

多个线程并发执行时,在默认情况下CPU是随机性的在线程之间进行切换的,但是有时候我们希望他们能有规律的执行,那么,多个线程之间就需要一些协调通信来改变或者控制CPU
的随机性,java提供了等待唤醒机制来解决这个问题,具体来说是多个线程依靠一个同步锁,然后借助 wait()和 notify()方法就可以实现线程间的协调通信.
同步锁相当于中间人的作用,多个线程必须用同一个同步锁(认识同一个中间人),只有同一个锁上的被等待的线程，才可以被持有该锁的另一个线程唤醒，使用不同锁的线程之间
不能相互唤醒，也就无法协调通信。                                     
   
<img src="https://upload-images.jianshu.io/upload_images/15181329-1888d5c432510843.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240">                                     

Java 在Object 类中提供了一些方法可以用来实现线程间的协调通:

* public final void wait(); 让当前线程释放锁
* public final native void wait(long timeout); 让当前线程释放锁，并等待xx 毫秒
* public final native void notify(); 唤醒持有同一锁的某个线程
* public final native void notifyAll(); 唤醒持有同一锁的所有线程

需要注意的是：在调用wait 和notify 方法时，当前线程必须已经持有锁，然后才可以调用，否则将会抛出IllegalMonitorStateException 异常。

**ThreadForNum1:**
```java
public class ThreadForNum1 extends Thread {

    public void run() {
        for (int i = 0; i < 11; i++) {
            synchronized (MyLock.o) {
                System.out.println("1");
                MyLock.o.notify();//唤醒另一个线程
                try {
                    MyLock.o.wait();//让自己休眠并释放锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```
**ThreadForNum2:**
```java
public class ThreadForNum2 extends Thread {

    public void run() {
        for (int i = 0; i < 11; i++) {
            synchronized (MyLock.o) {
                System.out.println("2");
                MyLock.o.notify();//唤醒另一个线程
                try {
                    MyLock.o.wait();//让自己休眠并释放锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```
**测试结果**
```
1
2
1
2
1
2
1
2
...
```

当出现三个线程或者三个以上的线程进行通信时就不能使用`notify()`方法了,在JDK1.5之前我们要使用`notifyAll()`, 用while来反复判断条件(不能使用if做条件判断),
因为 wait是在哪里等待就在哪里醒来

在JDK1.5之后:

    1.同步
    	 使用ReentrantLock类的lock()和unlock()方法进行同步
    2.通信
    	 使用ReentrantLock类的newCondition()方法可以获取Condition对象
    	 需要等待的时候使用Condition的await()方法, 唤醒的时候用signal()方法
    	 不同的线程使用不同的Condition, 这样就能区分唤醒的时候找哪个线程了

**代码**

<a href="https://github.com/haoxiaoyong1014/recording/blob/master/src/main/java/cn/haoxiaoyong/record/thread/Demo3_Printer.java">Demo3_Printer</a>

<a href="https://github.com/haoxiaoyong1014/recording/blob/master/src/test/java/cn/haoxiaoyong/record/Demo3_ReentrantLock.java">Demo3_ReentrantLock</a>    

**补充**

    1,在同步代码块中,用哪个对象锁,就用哪个对象调用 wait方法
    2,为什么wait方法和 notify方法定义在Object这个类中?
        因为锁对象可以是任意对象,Object是所有类的基类,所以 wait方法和notify方法需要定义在Object中
    3,sleep 方法和wait方法的区别?
        (1),sleep方法必须传入参数,参数就是时间,时间到了自动醒来
            wait方法可以传入参数也可以不用参入参数,传入参数就是在参数的时间结束后等待,不传入参数就是直接等待,
        (2),sleep 方法在同步函数或者或者同步代码块中,不释放锁,也就说是抱着锁睡
            wait方法在同步函数或者或者同步代码块中,释放锁    

<div id="live"></div>

#### 线程的生命周期

<img src="https://upload-images.jianshu.io/upload_images/15181329-25e3cd7ae5e7efd2.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240">

<div id="sq"></div>

#### 生产者消费者模式

该模式现实生活中很常见,在项目开发中也广泛使用,他是线程间通信的经典应用,生产者是一堆线程,消费者也是一堆线程,
内存缓冲区(容器)可以使用List集合,该模式的关键之处是如何处理多线程之间的协调通信,内存缓冲区为空的时候,消费者必须等待,
而内存缓冲区满的时候,生产者必须等待,一定要保持消费者和生产者的动态平衡.

下面的案例模拟实现农夫采摘水果放到筐里，小孩从筐里拿水果吃，农夫是一个线程，
小孩是一个线程，水果筐放满了，农夫停；水果筐空了，小孩停

A线程放水果，如果这时B线程要取水果，由于A没有释放锁，B线程处于等待状态，进入`阻塞队列`，放水果之后，要通知B线程取水果，
B线程进入`就绪队列`，反过来，B线程取水果，如果A线程要放水果，由于B线程没有释放锁，A线程处于等待状态，进入`阻塞队列`，
取水果之后，要通知A线程放水果，A线程进入`就绪队列`。我们希望当框里有水果时，A线程阻塞，B线程就绪，框里没水果时，A线程就绪，B线程阻塞


**Kuang**

```java
public class Kuang {
//这个集合就是水果筐假设最多存10 个水果
public static ArrayList<String> kuang=new ArrayList<String>();
}
```

上述代码定义一个静态集合作为内存缓冲区用来存储数据，同时这个集合也可以作为锁去被
多个线程使用。

**Farmer**

```java
public class Farmer extends Thread {

    @Override
    public void run() {
        while (true) {
            synchronized (Kuang.kuang) {
                //筐放满了就让农夫休息
                if (Kuang.kuang.size() == 10) {
                    try {
                        Kuang.kuang.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //2 往筐里放水果
                Kuang.kuang.add("apple");
                System.out.println("农夫放了一个水果,目前框里有" + Kuang.kuang.size() + "个水果");
                //3唤醒小孩继续吃
                Kuang.kuang.notify();
            }
            //模拟控制速度
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

上述代码就是农夫线程，不断的往集合（筐）里放水果，当筐满了就停，同时释放锁。

**Child**
```java
public class Child extends Thread {

    @Override
    public void run() {
        while (true) {
            synchronized (Kuang.kuang) {
                //1 框里没有水果就让小孩休息
                if (Kuang.kuang.size() == 0) {
                    try {
                        Kuang.kuang.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 2 小孩吃水果
                Kuang.kuang.remove("apple");
                System.out.println("小孩吃了一个水果,目前框里有" + Kuang.kuang.size() + "个水果");
                //唤醒农夫继续放水果
                Kuang.kuang.notify();
            }
            //模拟控制速度
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
```
上述代码是小孩线程，不断的从集合（筐）里拿水果吃，当筐空了就停，同时释放锁

**测试**
1,当农夫放水果的速度大于小孩吃的速度的时候
```
农夫放了一个水果,目前框里有1个水果
农夫放了一个水果,目前框里有2个水果
农夫放了一个水果,目前框里有3个水果
小孩吃了一个水果,目前框里有2个水果
农夫放了一个水果,目前框里有3个水果
农夫放了一个水果,目前框里有4个水果
农夫放了一个水果,目前框里有5个水果
农夫放了一个水果,目前框里有6个水果
小孩吃了一个水果,目前框里有5个水果
农夫放了一个水果,目前框里有6个水果
农夫放了一个水果,目前框里有7个水果
农夫放了一个水果,目前框里有8个水果
农夫放了一个水果,目前框里有9个水果
小孩吃了一个水果,目前框里有8个水果
```
2,当农夫放的水果的速度小于小孩吃的速度的时候

```
农夫放了一个水果,目前框里有1个水果
小孩吃了一个水果,目前框里有0个水果
农夫放了一个水果,目前框里有1个水果
小孩吃了一个水果,目前框里有0个水果
农夫放了一个水果,目前框里有1个水果
小孩吃了一个水果,目前框里有0个水果
农夫放了一个水果,目前框里有1个水果
小孩吃了一个水果,目前框里有0个水果
农夫放了一个水果,目前框里有1个水果
小孩吃了一个水果,目前框里有0个水果
农夫放了一个水果,目前框里有1个水果
小孩吃了一个水果,目前框里有0个水果
农夫放了一个水果,目前框里有1个水果
小孩吃了一个水果,目前框里有0个水果
农夫放了一个水果,目前框里有1个水果
小孩吃了一个水果,目前框里有0个水果
农夫放了一个水果,目前框里有1个水果
```

<div id="queue"></div>

#### 生产者-消费者与队列

<a href="https://github.com/haoxiaoyong1014/recording/blob/master/md/thread3.md">生产者-消费者与队列</a>


#### synchronized底层语义原理

<a href="https://github.com/haoxiaoyong1014/recording/blob/master/md/syn.md">synchronized底层语义原理</a>


[回到顶部](#top)
                                                  