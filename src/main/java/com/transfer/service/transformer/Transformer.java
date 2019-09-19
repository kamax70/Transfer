package com.transfer.service.transformer;

import java.io.Reader;

public interface Transformer {

    <T> String toJson(T ojb);

    <T> T fromJson(Reader reader, Class<T> clazz);
}
