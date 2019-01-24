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
