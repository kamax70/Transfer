package com.transfer.controller.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class InvalidRequestException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidRequestException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
