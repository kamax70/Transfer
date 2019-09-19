package com.transfer.service.transformer;

import com.google.gson.Gson;

import java.io.Reader;

public class GsonTransformer implements Transformer {

    private static final Gson gson = new Gson();

    @Override
    public <T> String toJson(T ojb) {
        return gson.toJson(ojb);
    }

    @Override
    public <T> T fromJson(Reader reader, Class<T> clazz) {
        return gson.fromJson(reader, clazz);
    }
}
