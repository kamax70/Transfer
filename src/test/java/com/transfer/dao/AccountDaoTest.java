package com.transfer.dao;

import com.transfer.AbstractDaoTest;
import com.transfer.service.model.AccountInfo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AccountDaoTest extends AbstractDaoTest {

    private AccountDao accountDao;
    private long account1;
    private long account2;
    private long balance1;
    private long balance2;

    @BeforeClass
    public void beforeClass() {
        this.accountDao = new AccountDaoImpl(dataSource);
    }

    @BeforeMethod
    public void beforeMethod() {
        account1 = nextLong();
        account2 = nextLong();
        balance1 = nextLong(500, 1000);
        balance2 = nextLong(500, 1000);

        accountDao.create(AccountInfo.builder()
                .accountNumber(account1)
                .balance(balance1)
                .build());
        accountDao.create(AccountInfo.builder()
                .accountNumber(account2)
                .balance(balance2)
                .build());

        AccountInfo accountInfo1 = accountDao.getByAccNumber(account1);
        assertNotNull(accountInfo1);
        assertEquals(accountInfo1.getAccountNumber(), account1);
        assertEquals(accountInfo1.getBalance(), balance1);

        AccountInfo accountInfo2 = accountDao.getByAccNumber(account2);
        assertNotNull(accountInfo2);
        assertEquals(accountInfo2.getAccountNumber(), account2);
        assertEquals(accountInfo2.getBalance(), balance2);
    }

    @Test(description = "valid update")
    public void case_01() {
        long shift = 100;

        int count = accountDao.updateByTransaction(account1, account2, shift);
        assertEquals(count, 2);

        AccountInfo byAccNumber1 = accountDao.getByAccNumber(account1);
        assertNotNull(byAccNumber1);
        assertEquals(byAccNumber1.getAccountNumber(), account1);
        assertEquals(byAccNumber1.getBalance(), balance1 - shift);

        AccountInfo byAccNumber2 = accountDao.getByAccNumber(account2);
        assertNotNull(byAccNumber2);
        assertEquals(byAccNumber2.getAccountNumber(), account2);
        assertEquals(byAccNumber2.getBalance(), balance2 + shift);
    }

    @Test(description = "account not exists")
    public void case_02() {
        long shift = 100;

        int count = accountDao.updateByTransaction(nextLong(), account2, shift);
        assertEquals(count, 0);

        count = accountDao.updateByTransaction(account1, nextLong(), shift);
        assertEquals(count, 0);
    }

    @Test(description = "no money")
    public void case_03() {
        long shift = 5000;

        int count = accountDao.updateByTransaction(account1, account2, shift);
        assertEquals(count, 0);
    }

    @Test(description = "no accountFrom")
    public void case_04() {
        long shift = 5000;

        int count = accountDao.updateByTransaction(nextLong(), account2, shift);
        assertEquals(count, 0);
    }

    @Test(description = "no accountTo")
    public void case_05() {
        long shift = 5000;

        int count = accountDao.updateByTransaction(nextLong(), account2, shift);
        assertEquals(count, 0);
    }
}