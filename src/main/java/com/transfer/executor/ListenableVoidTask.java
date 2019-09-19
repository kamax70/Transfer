package com.transfer.executor;

import java.util.concurrent.CompletableFuture;

final class ListenableVoidTask extends CompletableFuture<Void> implements Runnable {

    private final Runnable runnable;

    ListenableVoidTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Exception e) {
            completeExceptionally(e);
            return;
        }

        complete(null);
    }
}
