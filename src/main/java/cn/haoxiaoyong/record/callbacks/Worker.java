package cn.haoxiaoyong.record.callbacks;

import cn.haoxiaoyong.record.callbacks.impl.MyFetcher;
import cn.haoxiaoyong.record.callbacks.impl.service.Fetcher;
import cn.haoxiaoyong.record.callbacks.impl.service.FetcherCallback;

/**
 * Created by haoxy on 2019/3/2.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class Worker {

    public void doWork() {
        Fetcher fetcher = new MyFetcher(new Data(1, 0));
        fetcher.fetchData(new FetcherCallback() {

            @Override
            public void onData(Data data) throws Exception {
                System.out.println("Data received :" + data);
            }

            @Override
            public void onError(Throwable cause) {
                System.out.println("An error accour: " + cause.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        Worker worker = new Worker();
        worker.doWork();
    }
}
