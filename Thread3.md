
[生产者-消费者与队列](#queue2)

[使用线程池](#pool)

<div id="queue2"></div>

#### 生产者-消费者与队列

生产者消费者问题是一个典型的多进程同步问题

**问题陈述**

生产者和消费者两个程序,共享一个大小有限的公共缓冲区。

假设一个生产者“生产”一份数据并将其存储在缓冲区中，而一个消费者“消费”这份数据，并将这份数据从缓冲区中删除。

再假设现在这两个程序在并发地运行，我们需要确保当缓冲区的数据已满时，生产者不会放置新数据进来，也要确保当缓冲区的数据为空时，
消费者不会试图删除数据缓冲区的数据。  

**解决方案**

为了解决上述的并发问题，生产者和消费者将不得不相互通信。

如果缓冲区已满，生产者将处于睡眠状态，直到有通知信息唤醒。

在消费者将一些数据从缓冲区删除后，消费者将通知生产者，随后生产者将重新开始填充数据到缓冲区中。

如果缓冲区内容为空的化，那么情况是一样的，只不过，消费者会先等待生产者的通知。

但如果这种沟通做得不恰当，在进程彼此等待的位置可能导致程序死锁

**经典的方法**

```java
public class BufferProducerConsumerExample {

    public static void main(String[] args) throws InterruptedException {

        final Buffer buffer = new Buffer(10);
        Thread producerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    buffer.produce();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread comsumerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    buffer.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        producerThread.start();
        comsumerThread.start();
        producerThread.join();
        producerThread.join();
    }

}


public class Buffer {

    private Queue<Integer> list;

    private int size;

    public Buffer(int size) {
        this.list = new LinkedList<Integer>();
        this.size = size;
    }

    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (this) {
                while (list.size() >= size) {
                    //等待消费者
                    System.out.println("等待消费者");
                    wait();
                }
                list.add(value);
                System.out.println("生产 " + value);
                value++;
                //通知消费者
                notify();
                //Thread.sleep(1000);
            }
            Thread.sleep(1000);//放在同步方法中和放在同步方法外效果不一样
        }
    }

    public void consume() throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (list.size() == 0) {
                    //等待生产者
                    System.out.println("等待生产者");
                    wait();
                }
                int value = list.poll();
                System.out.println("消费 " + value);
                //通知生产者
                notify();
            }
            Thread.sleep(1000);
        }
    }
}

```
这里我们有生产者和消费者两个线程，它们共享一个公共缓冲区。生产者线程开始产生新的元素并将它们存储在缓冲区。
如果缓冲区已满，那么生产者线程进入睡眠状态，直到有通知唤醒。否则，生产者线程将会在缓冲区创建一个新元素然后通知消费者。
就像我之前说的，这个过程也适用于消费者。如果缓冲区为空，那么消费者将等待生产者的通知。否则，消费者将从缓冲区删除一个元素并通知生产者。

正如你所看到的，在之前的例子中，生产者和消费者的工作都是管理缓冲区的对象。
这些线程仅仅调用了buffer.produce()和buffer.consume()两个方法就搞定了一切。

**队列阻塞(BlockingQueue)**

不过，我们还可以进一步改善。

在前面的例子中，我们已经创建了一个缓冲区，每当存储一个元素之前，缓冲区将等待是否有可用的一个槽以防止没有足够的存储空间，
并且，在合并之前，缓冲区也会等待一个新的元素出现，以确保存储和删除的操作是线程安全的。

但是，Java本身的库已经整合了这些操作。它被称之为BlockingQueue

BlockingQueue是一个以线程安全的形式存入和取出实例的队列。而这就是我们所需要的。

所以,如果我们在示例中使用BlockingQueue，我们就不需要再去实现等待和通知的机制。

```java
public class ProducerConsumerWithBlockingQueue {

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<Integer> blockingQueue = new LinkedBlockingDeque<Integer>(10);

        Thread producerThread = new Thread(() -> {
            int value = 0;
            while (true) {
                try {
                    blockingQueue.put(value);
                    System.out.println("生产 " + value);
                    value++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread consumerThread = new Thread(() -> {
            while (true) {
                try {
                    int value = blockingQueue.take();
                    System.out.println("消费 " + value);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        producerThread.start();
        consumerThread.start();
        producerThread.join();
        consumerThread.join();

    }
}
```

**关于Blocking Queue的更多细节**

这儿有很多种类型的<a href="https://www.baeldung.com/java-blocking-queue">BlockingQueue</a> 

    * 无界队列
    
    * 有界队列
    
一个无界队列几乎可以无限地增加元素，任何添加操作将不会被阻止。

你可以以这种方式去创建一个无界队列：
```java
BlockingQueue blockingQueue = new LinkedBlockingDeque<>();
```    
在这种情况下,由于添加操作不会被阻塞,生产者添加新元素时可以不用等待。每次当生产者想要添加一个新元素时，会有一个队列先存储它。但是，
这里面也存在一个异常需要捕获。如果消费者删除元素的速度比生产者添加新的元素要慢，那么内存将被填满，我们将可能得到一个`OutOfMemory`异常。

与之相反的则是有界队列，存在一个固定大小。你可以这样去创建它：

```java
BlockingQueue blockingQueue = new LinkedBlockingDeque<>(10);
```
两者最主要的区别在于，使用有界队列的情况下，如果队列内存已满，而生产者仍然试图往里面塞元素，那么队列将会被阻塞（具体阻塞方式取决于添加元素的方法）直到有足够的空间腾出来。

往blocking queue里面添加元素一共有以下四种方式：

    * add() - 如果插入成功返回true，否则抛出IllegalStateException
    
    * put() - 在队列中插入一个元素，并在必要时等待一个空闲插槽
    
    * offer() - 如果插入元素成功返回true，否则返回false
    
    * offer(E e, long timeout, TimeUnit unit) – 在队列没有满的情况下，或者为了一个可用的slot而等待指定的时间后，往队列中插入一个元素。
    
所以，如果你使用put()方法插入元素，而队列内存已满的情况下，我们的生产者就必须等待，直到有可用的slot出现。    

<div id="pool"></div>

#### 使用线程池

还有什么地方我们可以优化的？那首先来分析一下我们干了什么，我们实例化了两个线程，一个被叫做生产者，专门往队列里面塞元素，另一个被叫做消费者，负责从队列里面删元素。

然而，好的软件技术表明，手动地去创建和销毁线程是不好的做法。首先创建线程是一项昂贵的任务，每创建一个线程，意味着要经历一遍下面的步骤：

    1,首先要分配内存给一个线程堆栈
    
    2,操作系统要创建一个原生线程对应于Java的线程
    
    3,跟这个线程相关的描述符被添加到JVM内部的数据结构中

我们的案例中用了几个线程是没有问题的，而那也是并发工作的方式之一。这里的问题是，我们是手动地去创建线程，这可以说是一次糟糕的实践。如果我们手动地创建线程，除了创建过程中的消耗外，
还有另一个问题，就是我们无法控制同时有多少个线程在运行。举个例子，如果同时有一百万次请求线上服务，那么每一次请求都会相应的创建一个线程，那么同时会有一百万个线程在后台运行，
这将会导致线程不足

因此，我们需要一种战略性管理线程的方法。这里是线程池。

线程池根据选定的策略处理线程的生命周期。它拥有有限数量的空闲线程，并在需要解决任务时重用它们。这样，我们不必每次都为新请求创建一个新线程，因此，我们可以避免线程饥饿

Java线程池的实现包括：

    1,一个任务队列
    
    2,一个工作线程集合
    
    3,一个线程工厂
    
    4,管理线程池状态的元数据
    
为了同时运行一些任务，你必须把他们先放到任务队列里。然后，当一个线程可用的时候，它将接收一个任务并运行它。可用的线程越多，并行执行的任务就越多。    


```java
public class ProducerConsumerExecutorService {
    public static void main(String[] args) {
        BlockingQueue<Integer> blockingQueue = new LinkedBlockingDeque<>(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Runnable producerTask = () -> {
            try {
                int value = 0;
                while (true) {
                    blockingQueue.put(value);
                    System.out.println("Produced " + value);
                    value++;
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Runnable consumerTask = () -> {
            try {
                while (true) {
                    int value = blockingQueue.take();
                    System.out.println("Consume " + value);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        executor.execute(producerTask);
        executor.execute(consumerTask);
        executor.shutdown();
    }
}
```

这里的区别在于，我们不在手动创建或运行消费者和生产者线程。我们建立一个线程池，它将收到两个任务，生产者和消费者的任务。生产者和消费者的任务，实际上跟之前例子里面使用的runnable是相同的。现在，执行程序(线程池实现)将接收任务，并安排它的工作线程去执行他们。

在我们简单的案例下，一切都跟之前一样运行。就像之前的例子，我们仍然有两个线程，他们仍然要以同样的方式生产和消费元素。虽然我们并没有让性能得到提升，但是代码看起来干净多了。我们不再手动创建线程，而只是具体说明我们想要什么：我们想要并发执行某些任务。

所以，当你使用一个线程池时。你不需要考虑线程是并发执行的单位，相反的，你把一些任务看作并发执行的就好。以上就是你需要知道的，剩下的由执行程序去处理。执行程序会收到一些任务，然后，它会分配工作线程去处理它们。