package com.transfer.config;

import lombok.AllArgsConstructor;

import java.util.Properties;

@AllArgsConstructor
public class PropertyManager {

    private final Properties properties;

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public Boolean getBoolean(String key) {
        try {
            return Boolean.valueOf(properties.getProperty(key));
        } catch (Throwable t) {
            return null;
        }
    }

    public Integer getInt(String key) {
        try {
            return Integer.valueOf(properties.getProperty(key));
        } catch (Throwable t) {
            return null;
        }
    }

    public Long getLong(String key) {
        try {
            return Long.valueOf(properties.getProperty(key));
        } catch (Throwable t) {
            return null;
        }
    }
}