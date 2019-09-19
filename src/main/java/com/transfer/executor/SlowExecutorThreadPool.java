package com.transfer.executor;

import com.transfer.executor.mdc.MdcThreadPoolExecutor;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

//Thread pool for low priority tasks
class SlowExecutorThreadPool extends MdcThreadPoolExecutor {

    SlowExecutorThreadPool(int threadCount) {
        super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS, new LinkedTransferQueue<>());
        setThreadFactory(new ExecutorThreadFactory("slow-thread-", Thread.NORM_PRIORITY - 1));
    }
}
