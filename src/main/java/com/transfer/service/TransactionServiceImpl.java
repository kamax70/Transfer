package com.transfer.service;

import com.transfer.controller.model.*;
import com.transfer.dao.AccountDao;
import com.transfer.dao.TransactionDao;
import com.transfer.executor.FutureDSL;
import com.transfer.service.model.TransactionInfo;
import com.transfer.service.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    @Override
    public FutureDSL createTransaction(CreateTransactionReq request) {
        return FutureDSL.fromTask(() -> {
            log.debug("Request for creating transaction from={} to={} shift={}", request.getAccountFrom(), request.getAccountTo(), request.getShift());
            if (request.getAccountFrom() == null) {
                throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "Account from must not be null");
            }
            if (request.getAccountTo() == null) {
                throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "Account to must not be null");
            }
            if (request.getShift() == null || request.getShift() <= 0) {
                throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "Shift must greater than 0");
            }
            if (request.getAccountFrom().longValue() == request.getAccountTo().longValue()) {
                throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "You can't transfer money on the same account");
            }
        })
                .thenApply(v -> {
                    log.debug("Transaction creating");
                    int updated = accountDao.updateByTransaction(request.getAccountFrom(), request.getAccountTo(), request.getShift());
                    long cur = System.currentTimeMillis();
                    if (updated == 0) {
                        log.debug("Transaction rejected");
                        throw new InvalidRequestException(ErrorCode.REJECTED, "Transaction rejected");
                    }
                    if (updated != 2) {
                        log.error("Unexpected updated count {}", updated);
                        throw new ExecutionException(ErrorCode.UNKNOWN_ERROR, "Internal error");
                    }
                    log.debug("Transaction created");
                    return cur;
                })
                .thenAccept(createTimestamp -> {
                    TransactionInfo transactionFrom = TransactionInfo.builder()
                            .timestamp(createTimestamp)
                            .accountNumber(request.getAccountFrom())
                            .transactionType(TransactionType.DECREASE)
                            .shift(request.getShift())
                            .build();
                    log.debug("Persist 'from' transaction {}", transactionFrom);
                    TransactionInfo transactionTo = TransactionInfo.builder()
                            .timestamp(createTimestamp)
                            .accountNumber(request.getAccountTo())
                            .transactionType(TransactionType.INCREASE)
                            .shift(request.getShift())
                            .build();
                    log.debug("Persist 'to' transaction {}", transactionTo);
                    transactionDao.create(transactionFrom);
                    log.debug("Transaction 'from' persisted");
                    transactionDao.create(transactionTo);
                    log.debug("Transaction 'to' persisted");
                });
    }

    @Override
    public FutureDSL getTransactionList(TransactionListReq request) {
        return FutureDSL.fromSlowTask(() -> {
            log.debug("Request for getting transaction list account={} limit={}", request.getAccount(), request.getLimit());
            if (request.getAccount() == null) {
                throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "Account to must not be null");
            }
            if (request.getLimit() == null || request.getLimit() <= 0) {
                throw new InvalidRequestException(ErrorCode.INVALID_ARGUMENT, "Shift must greater than 0");
            }
        })
                .thenApplySlow(v -> new TransactionListRes(transactionDao.getLast(request.getAccount(), request.getLimit())));
    }
}