package com.transfer.dao;

import com.transfer.service.model.TransactionInfo;

import java.util.List;

public interface TransactionDao {
    void create(TransactionInfo transactionInfo);

    List<TransactionInfo> getLast(long accountNumber, int limit);
}
