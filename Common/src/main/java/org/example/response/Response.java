package org.example.response;

import java.io.Serializable;

public record Response(Status status, Object body) implements Serializable {
    public static Response success(Object body) {
        return new Response(Status.OK, body);
    }

    public static Response created(Object body) {
        return new Response(Status.CREATED, body);
    }

    public static Response noContent() {
        return new Response(Status.NO_CONTENT, null);
    }
}
