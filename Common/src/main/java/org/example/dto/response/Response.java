package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.dto.Status;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Response implements Serializable {
    private final Status status;
    private final Object body;

    public static Response success(Object body) {
        return new Response(Status.OK, body);
    }
}
