package com.transfer.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class TransactionInfo {

    private long id;
    private long accountNumber;
    private long timestamp;
    private TransactionType transactionType;
    private long shift;
}