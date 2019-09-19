package com.transfer.controller;

import com.transfer.AbstractIntegrationTest;
import com.transfer.JsonWebClient;
import com.transfer.controller.model.CreateAccountReq;
import com.transfer.controller.model.CreateTransactionReq;
import com.transfer.controller.model.ErrorResponse;
import com.transfer.service.model.AccountInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class CreateTransactionTest extends AbstractIntegrationTest {

    private long accountFrom;
    private long accountTo;

    @BeforeMethod
    public void beforeMethod() {
        accountFrom = nextLong();
        accountTo = nextLong();

        CreateAccountReq createAccountReq = CreateAccountReq.builder()
                .accountNumber(accountFrom)
                .balance(500L)
                .build();
        JsonWebClient.postForObject(getAccountUrl(), createAccountReq, Void.class, 200);

        createAccountReq = CreateAccountReq.builder()
                .accountNumber(accountTo)
                .balance(200L)
                .build();
        JsonWebClient.postForObject(getAccountUrl(), createAccountReq, Void.class, 200);
    }


    @Test(description = "Valid flow")
    public void case_01() {
        CreateTransactionReq createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .shift(100L)
                .build();

        JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, Void.class, 200);

        AccountInfo accountInfo = JsonWebClient.getForObject(getAccountUrl() + "?id=" + accountFrom, AccountInfo.class, 200);
        assertNotNull(accountInfo);
        assertEquals(accountInfo.getAccountNumber(), accountFrom);
        assertEquals(accountInfo.getBalance(), 400);

        accountInfo = JsonWebClient.getForObject(getAccountUrl() + "?id=" + accountTo, AccountInfo.class, 200);
        assertNotNull(accountInfo);
        assertEquals(accountInfo.getAccountNumber(), accountTo);
        assertEquals(accountInfo.getBalance(), 300);
    }

    @Test(description = "Validate balance")
    public void case_02() {
        CreateTransactionReq createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .shift(600L)
                .build();

        ErrorResponse response = JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 5);
        assertEquals(response.getErrorMessage(), "Transaction rejected");

        createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .shift(-10L)
                .build();

        response = JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 4);
        assertEquals(response.getErrorMessage(), "Shift must greater than 0");

        createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .build();

        response = JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 4);
        assertEquals(response.getErrorMessage(), "Shift must greater than 0");
    }

    @Test(description = "Validate account from")
    public void case_03() {
        CreateTransactionReq createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(nextLong())
                .accountTo(accountTo)
                .shift(100L)
                .build();

        ErrorResponse response = JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 5);
        assertEquals(response.getErrorMessage(), "Transaction rejected");

        createTransactionReq = CreateTransactionReq.builder()
                .accountTo(accountTo)
                .shift(100L)
                .build();

        response = JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 4);
        assertEquals(response.getErrorMessage(), "Account from must not be null");
    }

    @Test(description = "Validate account to")
    public void case_04() {
        CreateTransactionReq createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(accountFrom)
                .accountTo(nextLong())
                .shift(100L)
                .build();

        ErrorResponse response = JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 5);
        assertEquals(response.getErrorMessage(), "Transaction rejected");

        createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(accountFrom)
                .shift(100L)
                .build();

        response = JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 4);
        assertEquals(response.getErrorMessage(), "Account to must not be null");
    }

    @Test(description = "Same account")
    public void case_05() {
        CreateTransactionReq createTransactionReq = CreateTransactionReq.builder()
                .accountFrom(accountFrom)
                .accountTo(accountFrom)
                .shift(100L)
                .build();

        ErrorResponse response = JsonWebClient.postForObject(getTransactionUrl(), createTransactionReq, ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 4);
        assertEquals(response.getErrorMessage(), "You can't transfer money on the same account");
    }
}