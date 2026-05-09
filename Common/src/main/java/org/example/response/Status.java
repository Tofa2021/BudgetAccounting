package org.example.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Status implements Serializable {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),

    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INVALID_TOKEN(408),

    UNKNOWN_SERVER_ERROR(500),
    ALREADY_EXISTS(500),
    CONNECTION_ERROR(501),
    ;

    private final int status;

    public boolean isSuccess() {
        return this == OK || this == CREATED || this == NO_CONTENT;
    }
}
