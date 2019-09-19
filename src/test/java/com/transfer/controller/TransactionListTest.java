package com.transfer.controller;

import com.transfer.AbstractIntegrationTest;
import com.transfer.JsonWebClient;
import com.transfer.controller.model.CreateAccountReq;
import com.transfer.controller.model.CreateTransactionReq;
import com.transfer.controller.model.ErrorResponse;
import com.transfer.controller.model.TransactionListRes;
import com.transfer.service.model.TransactionInfo;
import com.transfer.service.model.TransactionType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TransactionListTest extends AbstractIntegrationTest {

    private long account1;
    private long account2;

    @BeforeMethod
    public void beforeMethod() {
        account1 = nextLong();
        account2 = nextLong();

        CreateAccountReq createAccountReq = CreateAccountReq.builder()
                .accountNumber(account1)
                .balance(500L)
                .build();
        JsonWebClient.postForObject(getAccountUrl(), createAccountReq, Void.class, 200);

        createAccountReq = CreateAccountReq.builder()
                .accountNumber(account2)
                .balance(200L)
                .build();
        JsonWebClient.postForObject(getAccountUrl(), createAccountReq, Void.class, 200);

        CreateTransactionReq createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(account1)
                .accountTo(account2)
                .shift(100L)
                .build();

        JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, Void.class, 200);

        createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(account2)
                .accountTo(account1)
                .shift(200L)
                .build();

        JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, Void.class, 200);

        createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(account1)
                .accountTo(account2)
                .shift(300L)
                .build();

        JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, Void.class, 200);
    }


    @Test(description = "Valid flow")
    public void case_01() {
        TransactionListRes listRes = JsonWebClient.getForObject(String.format(getTransactionUrl() + "?account=%s&limit=%s", account1, 5), TransactionListRes.class, 200);
        assertNotNull(listRes);
        List<TransactionInfo> infos = listRes.getList();
        assertNotNull(infos);
        assertEquals(infos.size(), 3);

        TransactionInfo transactionInfo = infos.get(0);
        assertEquals(transactionInfo.getAccountNumber(), account1);
        assertEquals(transactionInfo.getShift(), 300);
        assertEquals(transactionInfo.getTransactionType(), TransactionType.DECREASE);

        transactionInfo = infos.get(1);
        assertEquals(transactionInfo.getAccountNumber(), account1);
        assertEquals(transactionInfo.getShift(), 200);
        assertEquals(transactionInfo.getTransactionType(), TransactionType.INCREASE);

        transactionInfo = infos.get(2);
        assertEquals(transactionInfo.getAccountNumber(), account1);
        assertEquals(transactionInfo.getShift(), 100);
        assertEquals(transactionInfo.getTransactionType(), TransactionType.DECREASE);

        //----

        listRes = JsonWebClient.getForObject(String.format(getTransactionUrl() + "?account=%s&limit=%s", account2, 5), TransactionListRes.class, 200);
        assertNotNull(listRes);
        infos = listRes.getList();
        assertNotNull(infos);
        assertEquals(infos.size(), 3);

        transactionInfo = infos.get(0);
        assertEquals(transactionInfo.getAccountNumber(), account2);
        assertEquals(transactionInfo.getShift(), 300);
        assertEquals(transactionInfo.getTransactionType(), TransactionType.INCREASE);

        transactionInfo = infos.get(1);
        assertEquals(transactionInfo.getAccountNumber(), account2);
        assertEquals(transactionInfo.getShift(), 200);
        assertEquals(transactionInfo.getTransactionType(), TransactionType.DECREASE);

        transactionInfo = infos.get(2);
        assertEquals(transactionInfo.getAccountNumber(), account2);
        assertEquals(transactionInfo.getShift(), 100);
        assertEquals(transactionInfo.getTransactionType(), TransactionType.INCREASE);
    }

    @Test(description = "Unknown account")
    public void case_02() {
        TransactionListRes listRes = JsonWebClient.getForObject(String.format(getTransactionUrl() + "?account=%s&limit=%s", nextLong(), 5), TransactionListRes.class, 200);
        assertNotNull(listRes);
        List<TransactionInfo> infos = listRes.getList();
        assertNotNull(infos);
        assertEquals(infos.size(), 0);
    }

    @Test(description = "Small limit")
    public void case_03() {
        TransactionListRes listRes = JsonWebClient.getForObject(String.format(getTransactionUrl() + "?account=%s&limit=%s", account1, 1), TransactionListRes.class, 200);
        assertNotNull(listRes);
        List<TransactionInfo> infos = listRes.getList();
        assertNotNull(infos);
        assertEquals(infos.size(), 1);

        TransactionInfo transactionInfo = infos.get(0);
        assertEquals(transactionInfo.getAccountNumber(), account1);
        assertEquals(transactionInfo.getShift(), 300);
        assertEquals(transactionInfo.getTransactionType(), TransactionType.DECREASE);
    }

    @Test(description = "bad request")
    public void case_04() {
        ErrorResponse response = JsonWebClient.getForObject(String.format(getTransactionUrl() + "?account=%s", account1), ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 4);
        assertEquals(response.getErrorMessage(), "Shift must greater than 0");

        response = JsonWebClient.getForObject(String.format(getTransactionUrl() + "?account=%s&limit=%s", account1, -1), ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 4);
        assertEquals(response.getErrorMessage(), "Shift must greater than 0");

        response = JsonWebClient.getForObject(String.format(getTransactionUrl() + "?limit=%s", 1), ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 4);
        assertEquals(response.getErrorMessage(), "Account to must not be null");
    }
}