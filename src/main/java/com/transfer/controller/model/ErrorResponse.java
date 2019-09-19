package com.transfer.controller.model;

import lombok.Data;

@Data
public class ErrorResponse {

    private final int errorCode;
    private final String errorMessage;
}
