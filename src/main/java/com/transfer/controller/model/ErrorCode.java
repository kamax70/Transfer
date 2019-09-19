package com.transfer.controller.model;

public enum ErrorCode {
    UNKNOWN_ERROR(1),
    INTERNAL_ERROR(2),
    INVALID_REQUEST(3),
    INVALID_ARGUMENT(4),
    REJECTED(5),
    ENTITY_NOT_FOUND(6);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public String getName() {
        return this.name();
    }

    public int getCode() {
        return this.code;
    }
}
