package com.transfer.service;

import com.transfer.controller.model.CreateAccountReq;
import com.transfer.controller.model.ErrorCode;
import com.transfer.controller.model.InvalidRequestException;
import com.transfer.dao.AccountDao;
import com.transfer.executor.FutureDSL;
import com.transfer.service.model.AccountInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;

    @Override
    public FutureDSL createAccount(CreateAccountReq request) {
        return FutureDSL.fromSlowTask(() -> {
            log.debug("Request for creating account id={} balance={}", request.getAccountNumber(), request.getBalance());
            if (request.getAccountNumber() == null) {
                throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "Account must not be null");
            }
            if (request.getBalance() == null || request.getBalance() < 0) {
                throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "Balance must 0 or greater");
            }
        })
                .thenApplySlow((v) -> accountDao.getByAccNumber(request.getAccountNumber()))
                .thenAcceptSlow(acc -> {
                    if (acc != null) {
                        throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "Account already exists");
                    }
                    log.debug("Creating account");
                    accountDao.create(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .balance(request.getBalance())
                            .build());
                    log.debug("Account created");
                });
    }

    @Override
    public FutureDSL getAccountById(Long accountId) {
        return FutureDSL.fromSlowTask(() -> {
            log.debug("Request for get account by id={}", accountId);
            if (accountId == null || accountId <= 0) {
                throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "Account id must 0 or greater");
            }
        })
                .thenApplySlow((v) -> {
                    AccountInfo accountInfo = accountDao.getByAccNumber(accountId);
                    if (accountInfo == null) {
                        throw new InvalidRequestException(ErrorCode.ENTITY_NOT_FOUND, "Account not found");
                    }
                    return accountInfo;
                });
    }
}