package com.transfer.executor;


import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;


class ExecutorDelegate implements Executor {

    private final ExecutorService executor;

    ExecutorDelegate(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    FutureDSL<Void> executeListen(Runnable runnable) {
        ListenableVoidTask task = new ListenableVoidTask(runnable);
        executor.submit(task);
        return FutureDSL.fromFuture(task);

    }

    <T> FutureDSL<T> executeListen(Callable<T> callable) {
        ListenableResultTask<T> task = new ListenableResultTask<>(callable);
        executor.submit(task);
        return FutureDSL.fromFuture(task);

    }

}
