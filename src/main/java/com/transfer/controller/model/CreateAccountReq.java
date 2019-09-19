package com.transfer.controller.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAccountReq {

    private final Long accountNumber;
    private final Long balance;
}
