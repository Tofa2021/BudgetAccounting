package org.example.request;

public record RequestEnvelope(String accessToken, Request request) {
    public boolean isAuthenticated() {
        return accessToken != null && !accessToken.isBlank();
    }
}
