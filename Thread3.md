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

**使用线程池**

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