package org.example.dto.response;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public record Response(Status status, Object body) implements Serializable {
    public static Response success(Object body) {
        return new Response(Status.OK, body);
    }
}
