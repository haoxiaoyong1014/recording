package cn.haoxiaoyong.record.callbacks.impl.service;

import cn.haoxiaoyong.record.callbacks.Data;

/**
 * Created by haoxy on 2019/3/2.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public interface FetcherCallback {

    void onData(Data data) throws Exception;
    void onError(Throwable cause);
}
