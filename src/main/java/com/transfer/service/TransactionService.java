package com.transfer.service;

import com.transfer.controller.model.CreateTransactionReq;
import com.transfer.controller.model.TransactionListReq;
import com.transfer.executor.FutureDSL;

public interface TransactionService {
    FutureDSL createTransaction(CreateTransactionReq request);

    FutureDSL getTransactionList(TransactionListReq request);
}
