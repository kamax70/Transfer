package com.transfer.controller;

import com.transfer.AbstractIntegrationTest;
import com.transfer.JsonWebClient;
import com.transfer.controller.model.CreateAccountReq;
import com.transfer.controller.model.ErrorCode;
import com.transfer.controller.model.ErrorResponse;
import com.transfer.service.model.AccountInfo;
import org.testng.annotations.Test;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.testng.Assert.*;

public class CreateAccountTest extends AbstractIntegrationTest {

    @Test(description = "Valid flow")
    public void case_01() {
        long accNumber = nextLong();

        CreateAccountReq createAccountReq = CreateAccountReq.builder()
                .accountNumber(accNumber)
                .balance(500L)
                .build();
        JsonWebClient.postForObject(getAccountUrl(), createAccountReq, Void.class, 200);

        AccountInfo accountInfo = JsonWebClient.getForObject(getAccountUrl() + "?id=" + accNumber, AccountInfo.class, 200);
        assertNotNull(accountInfo);
        assertEquals(accountInfo.getAccountNumber(), accNumber);
        assertEquals(accountInfo.getBalance(), 500L);
        assertTrue(accountInfo.getId() > 0);

        //Error on existing account
        ErrorResponse errorResponse = JsonWebClient.postForObject(getAccountUrl(), createAccountReq, ErrorResponse.class, 400);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getErrorCode(), ErrorCode.INVALID_ARGUMENT.getCode());
        assertEquals(errorResponse.getErrorMessage(), "Account already exists");

        //Error on negative balance
        createAccountReq = CreateAccountReq.builder()
                .accountNumber(accNumber)
                .balance(-10L)
                .build();
        errorResponse = JsonWebClient.postForObject(getAccountUrl(), createAccountReq, ErrorResponse.class, 400);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getErrorCode(), ErrorCode.INVALID_ARGUMENT.getCode());
        assertEquals(errorResponse.getErrorMessage(), "Balance must 0 or greater");

        errorResponse = JsonWebClient.postForObject(getAccountUrl(), new Object(), ErrorResponse.class, 400);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getErrorCode(), ErrorCode.INVALID_ARGUMENT.getCode());
        assertEquals(errorResponse.getErrorMessage(), "Account must not be null");
    }

    @Test(description = "Get unknown acc")
    public void case_02() {
        ErrorResponse response = JsonWebClient.getForObject(getAccountUrl() + "?id=" + nextLong(), ErrorResponse.class, 400);
        assertNotNull(response);
        assertEquals(response.getErrorCode(), 6);
        assertEquals(response.getErrorMessage(), "Account not found");
    }
}