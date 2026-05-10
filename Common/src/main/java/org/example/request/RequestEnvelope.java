package org.example.request;

import java.io.Serializable;

public record RequestEnvelope(String accessToken, Request request) implements Serializable {
    public boolean isAuthenticated() {
        return accessToken != null && !accessToken.isBlank();
    }
}
