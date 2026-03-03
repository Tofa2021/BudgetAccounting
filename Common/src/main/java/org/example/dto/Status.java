package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Status implements Serializable {
    OK(200),
    SERVER_ERROR(500),
    CONNECTION_ERROR(501),
    ;

    private final int status;
}
