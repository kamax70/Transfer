package com.transfer.controller.model;

import lombok.Getter;

public class ExecutionResult<T> {

    private static final ExecutionResult EMPTY = new ExecutionResult();

    @Getter
    private final T response;

    private ExecutionResult() {
        this.response = null;
    }

    public ExecutionResult(T response) {
        this.response = response;
    }

    public static <T> ExecutionResult<T> of(T response) {
        return new ExecutionResult<>(response);
    }

    public static <T> ExecutionResult<T> empty() {
        return EMPTY;
    }

}
