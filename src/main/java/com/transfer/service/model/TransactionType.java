package com.transfer.service.model;

import lombok.Getter;

public enum  TransactionType {

    INCREASE(1), DECREASE(2);

    @Getter
    private int type;

    TransactionType(int type) {
        this.type = type;
    }

    public static TransactionType of(int type) {
        for (TransactionType tt : TransactionType.values()) {
            if (tt.type == type) {
                return tt;
            }
        }
        throw new RuntimeException("Unsupported transaction type");
    }
}
