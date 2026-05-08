package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Status implements Serializable {
    OK(200),

    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INVALID_TOKEN(408),

    SERVER_ERROR(500),
    ALREADY_EXISTS(500),
    CONNECTION_ERROR(501),
    FORBITTEN(403);

    private final int status;
}
