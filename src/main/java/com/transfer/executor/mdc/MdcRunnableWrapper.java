package com.transfer.executor.mdc;

public class MdcRunnableWrapper implements Runnable {
    private Runnable delegate;
    private String requestId;

    public MdcRunnableWrapper(Runnable delegate) {
        this.delegate = delegate;
        this.requestId = MdcUtil.getRequestIdOrNull(); //Save request UUID on wrap
    }

    public void run() {
        try {
            MdcUtil.setupRequestId(this.requestId); //Restore request UUID on execute
            this.delegate.run();
        } finally {
            MdcUtil.clear();
        }

    }
}