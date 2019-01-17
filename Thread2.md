我相信很多人都跟我一样，JavaSE基础没打牢，就急忙忙、兴冲冲的搞JavaEE了，
然后学习一下前台开发(html、css、javascript)，有可能还搞搞jquery、extjs，再然后是Struts、hibernate、spring，
然后听说找工作得会linux、oracle，又去学，在这个过程中，是否迷失了，虽然学习面很广，但就像《神雕侠侣》中黄药师评价杨过，**博而不精、杂而不纯**

回归正题，当我们查看JDK API的时候，总会发现一些类说明写着，线程安全或者线程不安全，比如说StringBuilder中，有这么一句，“将StringBuilder 的实例用于多个线程是不安全的。如果需要这样的同步，则建议使用StringBuffer。 
那么下面手动创建一个线程不安全的类，然后在多线程中使用这个类，看看有什么效果。

**Count**
```java
public class Count {

    private int num;
    public void increment() {
        num++;
    }
    public int get() {
        return num;
    }

}
```

**测试类**

```java
public class ThreadCountTest {

    public static void main(String[] args) {
        final Count count = new Count();
        Runnable runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    count.increment();
                }

            }
        };
        List<Thread> threads = new ArrayList<Thread>(10);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(runnable);
            threads.add(thread);
            thread.start();
        }
        while (true) {
            if (allThreadTerminated(threads)){//所有线程运行结束
                System.out.println(count.get());
                break;
            }
        }
    }

    private static boolean allThreadTerminated(List<Thread> threads) {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                return false;
            }
        }
        return true;
    }
}

```

这里启动了10个线程，每个线程累加1万次，我们期望的最终结果是10万，看一下输出结果：`73411`,在我的电脑上运行，大多数情况下都会得到一个小于10万的值，
那么想要得到我们期望的结果，就需要保证Count在多线程下使用是安全的，

要说明线程同步问题首先要说明Java线程的两个特性，可见性和有序性。多个线程之间是不能直接传递数据交互的，它们之间的交互只能通过共享变量来实现。拿上篇博文中的例子来说明，
在多个线程之间共享了Count类的一个对象，这个对象是被创建在主内存(堆内存)中，每个线程都有自己的工作内存(线程栈)，工作内存存储了主内存Count对象的一个副本，当线程操作Count对象时，
首先从主内存复制Count对象到工作内存中，然后执行代码count.increment()，改变了num值，最后用工作内存Count刷新主内存Count。当一个对象在多个内存中都存在副本时，如果一个内存修改了共享变量，其它线程也应该能够看到被修改后的值，此为可见性。多个线程执行时，
CPU对线程的调度是随机的，我们不知道当前程序被执行到哪步就切换到了下一个线程，一个最经典的例子就是银行汇款问题，一个银行账户存款100，这时一个人从该账户取10元，同时另一个人向该账户汇10元，那么余额应该还是100。那么此时可能发生这种情况，A线程负责取款，B线程负责汇款，
A从主内存读到100，B从主内存读到100，A执行减10操作，并将数据刷新到主内存，这时主内存数据100-10=90，而B内存执行加10操作，并将数据刷新到主内存，最后主内存数据100+10=110，显然这是一个严重的问题，我们要保证A线程和B线程有序执行，先取款后汇款或者先汇款后取款，此为有序性。

