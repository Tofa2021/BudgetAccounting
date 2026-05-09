package org.example.request;

import java.io.Serializable;
import java.util.Map;

public record Request(RequestAction action, Map<String, Object> params) implements Serializable {
    public <T> T getParam(String key) {
        return (T) params.get(key);
    }
}
