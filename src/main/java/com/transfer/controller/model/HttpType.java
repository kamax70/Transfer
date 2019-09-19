package com.transfer.controller.model;

public enum HttpType {
    GET,
    POST,
    PUT,
    DELETE;

    public boolean is(String method) {
        return name().equalsIgnoreCase(method);
    }
}
