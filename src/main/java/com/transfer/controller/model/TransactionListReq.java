package com.transfer.controller.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionListReq {

    private final Long account;
    private final Integer limit;
}