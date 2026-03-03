package org.example.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.dto.RequestAction;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Request implements Serializable {
    private final RequestAction action;
}
