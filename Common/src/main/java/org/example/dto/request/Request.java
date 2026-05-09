package org.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
@AllArgsConstructor
public class Request implements Serializable {
    private final RequestAction action;
    private final Map<String, Object> params;

    public <T> T getParam(String key) {
        return (T) params.get(key);
    }
}
