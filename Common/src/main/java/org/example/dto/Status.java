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
    INVALID_TOKEN(408),
    NOT_FOUND(404),
    ALREADY_EXISTS(500),
    ;

    private final int status;
}
