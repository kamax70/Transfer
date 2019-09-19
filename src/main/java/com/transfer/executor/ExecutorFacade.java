package com.transfer.executor;

import lombok.Getter;

import java.util.concurrent.Callable;

@Getter
class ExecutorFacade {

    private static final ExecutorFacade EXECUTOR_FACADE = new ExecutorFacade();

    private final ExecutorDelegate mainDelegate;
    private final ExecutorDelegate slowDelegate;

    public static ExecutorFacade get() {
        return EXECUTOR_FACADE;
    }

    private ExecutorFacade() {
        MainExecutorThreadPool mainExecutorThreadPool = new MainExecutorThreadPool(20);
        SlowExecutorThreadPool slowExecutorThreadPool = new SlowExecutorThreadPool(8);
        this.mainDelegate = new ExecutorDelegate(mainExecutorThreadPool);
        this.slowDelegate = new ExecutorDelegate(slowExecutorThreadPool);
    }

    FutureDSL<Void> executeListen(Runnable runnable) {
        return mainDelegate.executeListen(runnable);
    }

    <T> FutureDSL<T> executeListen(Callable<T> callable) {
        return mainDelegate.executeListen(callable);
    }

    FutureDSL<Void> executeSlowListen(Runnable runnable) {
        return slowDelegate.executeListen(runnable);
    }

    <T> FutureDSL<T> executeSlowListen(Callable<T> callable) {
        return slowDelegate.executeListen(callable);
    }

}
