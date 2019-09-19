package com.transfer.executor;

import com.transfer.executor.mdc.MdcThreadPoolExecutor;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

//Thread pool for high priority tasks
class MainExecutorThreadPool extends MdcThreadPoolExecutor {

    MainExecutorThreadPool(int threadCount) {
        super(threadCount, threadCount, 0, TimeUnit.MILLISECONDS, new LinkedTransferQueue<>());
        setThreadFactory(new ExecutorThreadFactory("main-thread-", Thread.NORM_PRIORITY + 1));
    }
}
