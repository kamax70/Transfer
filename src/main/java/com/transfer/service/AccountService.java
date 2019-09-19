package com.transfer.service;

import com.transfer.controller.model.CreateAccountReq;
import com.transfer.executor.FutureDSL;

public interface AccountService {
    FutureDSL createAccount(CreateAccountReq request);

    FutureDSL getAccountById(Long accountId);

}