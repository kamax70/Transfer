package com.transfer.executor.mdc;

import java.util.concurrent.*;

//Wrap all Runnable and Callable for saving request UUID
public class MdcThreadPoolExecutor extends ThreadPoolExecutor {

    public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public void execute(Runnable task) {
        super.execute(new MdcRunnableWrapper(task));
    }

    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new MdcCallableWrapper<>(task));
    }

    public Future<?> submit(Runnable task) {
        return super.submit(new MdcRunnableWrapper(task));
    }

    public <T> Future<T> submit(Runnable task, T t) {
        return super.submit(new MdcRunnableWrapper(task), t);
    }



}
