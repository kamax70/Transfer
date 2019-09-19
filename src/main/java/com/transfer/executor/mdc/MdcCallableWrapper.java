package com.transfer.executor.mdc;


import java.util.concurrent.Callable;

public class MdcCallableWrapper<V> implements Callable<V> {
    private Callable<V> delegate;
    private String requestId;

    public MdcCallableWrapper(Callable<V> delegate) {
        this.delegate = delegate;
        this.requestId = MdcUtil.getRequestIdOrNull(); //Save request UUID on wrap
    }

    public V call() throws Exception {
        V result;
        try {
            MdcUtil.setupRequestId(this.requestId); //Restore request UUID on execute
            result = this.delegate.call();
        } finally {
            MdcUtil.clear();
        }

        return result;
    }
}