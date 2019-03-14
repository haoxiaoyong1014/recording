package cn.haoxiaoyong.record.callbacks.impl;

import cn.haoxiaoyong.record.callbacks.Data;
import cn.haoxiaoyong.record.callbacks.impl.service.Fetcher;
import cn.haoxiaoyong.record.callbacks.impl.service.FetcherCallback;

/**
 * Created by haoxy on 2019/3/2.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class MyFetcher implements Fetcher {

     Data data;

    public MyFetcher(Data data) {
        this.data = data;
    }

    @Override
    public void fetchData(FetcherCallback callback) {
        try {
            callback.onData(data);
        } catch (Exception e) {
            callback.onError(e);
        }
    }
}
