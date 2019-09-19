package com.transfer.executor;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

//Facade for delegating execution to different Thread pools. Based on CompletableFuture
public class FutureDSL<T> extends CompletableFuture<T> {

    private static final ExecutorFacade EXECUTOR_FACADE = ExecutorFacade.get();

    private final ExecutorFacade executorFacade;
    private final CompletableFuture<T> future;

    private FutureDSL(ExecutorFacade executorFacade, CompletableFuture<T> future) {
        this.executorFacade = executorFacade;
        this.future = future;
    }

    // We must override this method because of "thenCompose" correct work
    @Override
    public CompletableFuture<T> toCompletableFuture() {
        return future;
    }

    public static FutureDSL<Void> fromTask(Runnable execution) {
        return EXECUTOR_FACADE.executeListen(execution);
    }

    public static <S> FutureDSL<S> fromTask(Callable<S> execution) {
        return EXECUTOR_FACADE.executeListen(execution);
    }

    public static FutureDSL<Void> fromSlowTask(Runnable execution) {
        return EXECUTOR_FACADE.executeSlowListen(execution);
    }

    public static <S> FutureDSL<S> fromSlowTask(Callable<S> execution) {
        return EXECUTOR_FACADE.executeSlowListen(execution);
    }

    public static <S> FutureDSL<S> fromConstant(S value) {
        return new FutureDSL<>(EXECUTOR_FACADE, CompletableFuture.completedFuture(value));
    }

    public static <S> FutureDSL<S> fromFuture(CompletableFuture<S> future) {
        if (future instanceof FutureDSL) {
            return (FutureDSL<S>) future;
        } else {
            return new FutureDSL<>(EXECUTOR_FACADE, future);
        }
    }

    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    public <U> FutureDSL<U> thenApply(Function<? super T, ? extends U> fn) {
        return new FutureDSL<>(executorFacade, future.thenApplyAsync(fn, executorFacade.getMainDelegate()));
    }

    public <U> FutureDSL<U> thenApplySlow(Function<? super T, ? extends U> fn) {
        return new FutureDSL<>(executorFacade, future.thenApplyAsync(fn, executorFacade.getSlowDelegate()));
    }

    public FutureDSL<Void> thenAccept(Consumer<? super T> action) {
        return new FutureDSL<>(executorFacade, future.thenAcceptAsync(action, executorFacade.getMainDelegate()));
    }

    public FutureDSL<Void> thenAcceptSlow(Consumer<? super T> action) {
        return new FutureDSL<>(executorFacade, future.thenAcceptAsync(action, executorFacade.getSlowDelegate()));
    }

    public <U> FutureDSL<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return new FutureDSL<>(executorFacade, future.thenCompose(fn));
    }

    public FutureDSL<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return new FutureDSL<>(executorFacade, future.exceptionally(t -> fn.apply(unwrapCompletionException(t))));
    }

    private static Throwable unwrapCompletionException(Throwable throwable) {
        return throwable instanceof CompletionException ? throwable.getCause() : throwable;
    }

    //Possible to implement all methods from CompletionStage if needed
}
