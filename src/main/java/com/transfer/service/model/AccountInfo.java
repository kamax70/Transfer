package com.transfer.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class AccountInfo {

    private final long id;
    private final long accountNumber;
    private final long balance;
}