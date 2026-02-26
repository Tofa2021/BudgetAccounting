package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
@AllArgsConstructor
public class Request implements Serializable {
    private final String action;
    private final Map<String, Object> params;
}
