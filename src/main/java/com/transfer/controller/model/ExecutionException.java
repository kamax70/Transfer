package com.transfer.controller.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ExecutionException extends RuntimeException {

    private final ErrorCode errorCode;

    public ExecutionException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
