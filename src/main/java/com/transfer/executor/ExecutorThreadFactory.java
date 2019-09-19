package com.transfer.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class ExecutorThreadFactory implements ThreadFactory {

    private final AtomicInteger counter;
    private final String prefix;
    private final int priority;


    ExecutorThreadFactory(String prefix, int priority) {
        this.prefix = prefix;
        this.priority = priority;
        this.counter = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(prefix + counter.getAndIncrement());
        thread.setPriority(priority);
        thread.setDaemon(true);
        return thread;
    }
}
