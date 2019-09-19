package com.transfer.executor;

import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;

import static org.testng.Assert.assertEquals;

public class FutureDSLTest {

    @Test
    public void case_01() throws ExecutionException, InterruptedException {
        int i = FutureDSL.fromConstant(1)
                .thenApply(v -> v + 1)
                .thenApplySlow(v -> v + 1)
                .thenCompose(v -> FutureDSL.fromTask(() -> v + 1))
                .thenCompose(v -> FutureDSL.fromSlowTask(() -> v + 1))
                .thenCompose(v -> FutureDSL.fromSlowTask(() -> v + 1))
                .get();
        assertEquals(i, 6);

        i = FutureDSL.fromFuture(FutureDSL.fromConstant(1))
                .thenApply(v -> v + 1)
                .thenApplySlow(v -> v + 1)
                .thenCompose(v -> FutureDSL.fromTask(() -> v + 1))
                .thenCompose(v -> FutureDSL.fromSlowTask(() -> v + 1))
                .thenCompose(v -> FutureDSL.fromSlowTask(() -> v + 1))
                .get();
        assertEquals(i, 6);

        FutureDSL.fromConstant(1)
                .thenAccept(v -> {}).get();

        FutureDSL.fromConstant(1)
                .thenAcceptSlow(v -> {}).get();

        i = FutureDSL.fromConstant(1)
                .thenApply(v -> {
                    if (true) {
                        throw new RuntimeException();
                    }
                    return v;
                })
                .exceptionally(t -> 2).get();
        assertEquals(i, 2);

        //compatibility with CompletableFuture
        i = FutureDSL.completedFuture(1)
                .thenApplyAsync(v -> v + 1)
                .thenApply(v -> v + 1).get();
        assertEquals(i, 3);
    }
}