package com.transfer.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

final class ListenableResultTask<T> extends CompletableFuture<T> implements Runnable {

    private final Callable<T> callable;

    ListenableResultTask(Callable<T> callable) {
        this.callable = callable;
    }

    @Override
    public void run() {
        T result;
        try {
            result = callable.call();
        } catch (Exception e) {
            completeExceptionally(e);
            return;
        }

        complete(result);
    }
}
