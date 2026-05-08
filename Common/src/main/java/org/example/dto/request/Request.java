package org.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Request implements Serializable {
    private final RequestAction action;
}
