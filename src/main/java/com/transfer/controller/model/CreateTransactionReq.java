package com.transfer.controller.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTransactionReq {

    private final Long accountFrom;
    private final Long accountTo;
    private final Long shift;
}
