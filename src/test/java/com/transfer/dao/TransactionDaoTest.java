package com.transfer.dao;

import com.transfer.AbstractDaoTest;
import com.transfer.service.model.AccountInfo;
import com.transfer.service.model.TransactionInfo;
import com.transfer.service.model.TransactionType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TransactionDaoTest extends AbstractDaoTest {

    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private long account;
    private TransactionType tt1;
    private TransactionType tt2;
    private TransactionType tt3;
    private long t1;
    private long t2;
    private long t3;
    private long shift1;
    private long shift2;
    private long shift3;

    @BeforeClass
    public void beforeClass() {
        this.accountDao = new AccountDaoImpl(dataSource);
        this.transactionDao = new TransactionDaoImpl(dataSource);
    }

    @BeforeMethod
    public void beforeMethod() {
        account = nextLong();
        accountDao.create(AccountInfo.builder()
                .accountNumber(account)
                .balance(nextLong())
                .build());

        long cur = System.currentTimeMillis();
        t1 = cur + 100;
        t2 = cur + 200;
        t3 = cur + 500;

        tt1 = TransactionType.INCREASE;
        tt2 = TransactionType.DECREASE;
        tt3 = TransactionType.INCREASE;

        shift1 = nextLong();
        shift2 = nextLong();
        shift3 = nextLong();

        transactionDao.create(TransactionInfo.builder()
                .accountNumber(account)
                .transactionType(tt1)
                .shift(shift1)
                .timestamp(t1)
                .build());
        transactionDao.create(TransactionInfo.builder()
                .accountNumber(account)
                .transactionType(tt2)
                .shift(shift2)
                .timestamp(t2)
                .build());
        transactionDao.create(TransactionInfo.builder()
                .accountNumber(account)
                .transactionType(tt3)
                .shift(shift3)
                .timestamp(t3)
                .build());
    }

    @Test(description = "Get all data")
    public void case_01() {
        List<TransactionInfo> list = transactionDao.getLast(account, 5);
        assertNotNull(list);
        assertEquals(list.size(), 3);

        TransactionInfo info = list.get(0);
        assertEquals(info.getAccountNumber(), account);
        assertEquals(info.getTimestamp(), t3);
        assertEquals(info.getTransactionType(), tt3);
        assertEquals(info.getShift(), shift3);

        info = list.get(1);
        assertEquals(info.getAccountNumber(), account);
        assertEquals(info.getTimestamp(), t2);
        assertEquals(info.getTransactionType(), tt2);
        assertEquals(info.getShift(), shift2);

        info = list.get(2);
        assertEquals(info.getAccountNumber(), account);
        assertEquals(info.getTimestamp(), t1);
        assertEquals(info.getTransactionType(), tt1);
        assertEquals(info.getShift(), shift1);
    }

    @Test(description = "Unknown account")
    public void case_02() {
        List<TransactionInfo> list = transactionDao.getLast(nextLong(), 5);
        assertNotNull(list);
        assertEquals(list.size(), 0);
    }

}
