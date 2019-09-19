package com.transfer.controller;

import com.transfer.AbstractIntegrationTest;
import com.transfer.JsonWebClient;
import com.transfer.controller.model.CreateAccountReq;
import com.transfer.controller.model.CreateTransactionReq;
import com.transfer.service.model.AccountInfo;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class PerformanceTest extends AbstractIntegrationTest {

    @Test(description = "Max contention, 9.5 sec for 10k operations on my machine")
    public void case_01() throws InterruptedException {
        int count = 1_000;
        long accountFrom = nextLong();
        long accountTo = nextLong();

        CreateAccountReq createAccountReq = CreateAccountReq.builder()
                .accountNumber(accountFrom)
                .balance((long) count)
                .build();
        JsonWebClient.postForObject(getAccountUrl(), createAccountReq, Void.class, 200);

        createAccountReq = CreateAccountReq.builder()
                .accountNumber(accountTo)
                .balance(0L)
                .build();
        JsonWebClient.postForObject(getAccountUrl(), createAccountReq, Void.class, 200);

        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch countDownLatch = new CountDownLatch(count);

        long cur = System.currentTimeMillis();
        IntStream.range(0, count).forEach(i -> executor.submit(() -> {
            CreateTransactionReq createTransactionReq = CreateTransactionReq.builder()
                    .accountFrom(accountFrom)
                    .accountTo(accountTo)
                    .shift(1L)
                    .build();
            try {
                JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, Void.class, 200);
            } finally {
                countDownLatch.countDown();
            }
        }));

        countDownLatch.await();
        System.out.println("Time: " + (System.currentTimeMillis() - cur));

        AccountInfo accountInfo = JsonWebClient.getForObject(getAccountUrl() + "?id=" + accountFrom, AccountInfo.class, 200);
        assertNotNull(accountInfo);
        assertEquals(accountInfo.getAccountNumber(), accountFrom);
        assertEquals(accountInfo.getBalance(), 0);

        accountInfo = JsonWebClient.getForObject(getAccountUrl() + "?id=" + accountTo, AccountInfo.class, 200);
        assertNotNull(accountInfo);
        assertEquals(accountInfo.getAccountNumber(), accountTo);
        assertEquals(accountInfo.getBalance(), count);
    }


    @Test(description = "Min contention, 6.5 sec for 10k operations on my machine")
    public void case_02() throws InterruptedException {
        int count = 1_000;

        LongStream.rangeClosed(1, count).forEach(acc -> {
            CreateAccountReq createAccountReq = CreateAccountReq.builder()
                    .accountNumber(acc)
                    .balance((long) count)
                    .build();
            JsonWebClient.postForObject(getAccountUrl(), createAccountReq, Void.class, 200);
        });

        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch countDownLatch = new CountDownLatch(count);

        long cur = System.currentTimeMillis();
        IntStream.range(0, count).forEach(i -> executor.submit(() -> {
            long acc = nextLong(1, count - 1);
            CreateTransactionReq createTransactionReq = CreateTransactionReq.builder()
                    .accountFrom(acc)
                    .accountTo(acc + 1)
                    .shift(1L)
                    .build();
            try {
                JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, Void.class, 200);
            } finally {
                countDownLatch.countDown();
            }
        }));

        countDownLatch.await();
        System.out.println("Time: " + (System.currentTimeMillis() - cur));
    }
}