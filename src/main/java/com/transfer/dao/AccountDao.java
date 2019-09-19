package com.transfer.dao;

import com.transfer.service.model.AccountInfo;

public interface AccountDao {
    void create(AccountInfo accountInfo);

    int updateByTransaction(long accountNumberFrom, long accountNumberTo, long shift);

    AccountInfo getByAccNumber(long accountNumber);
}
