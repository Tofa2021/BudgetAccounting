package org.example.request;

import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
public record Request(RequestAction action, Map<String, Object> params) implements Serializable {
    public <T> T getParam(String key) {
        return (T) params.get(key);
    }
}
