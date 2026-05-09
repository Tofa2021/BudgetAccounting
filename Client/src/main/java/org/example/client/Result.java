package org.example.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.response.Status;

@Getter
@AllArgsConstructor
public class Result<T> {
    private Status status;
    @Getter(AccessLevel.NONE)
    private T data;
    private String errorMessage;

    private Result() {
    }

    public static <T> Result<T> success(Status status, T data) {
        return new Result<>(status, data, "");
    }

    public static <T> Result<T> error(Status status, String errorMessage) {
        return new Result<>(status, null, errorMessage);
    }

    public boolean isSuccess() {
        return status.isSuccess();
    }

    public T getData() {
        if (isSuccess()) {
            return data;
        }
        throw new IllegalStateException("Error response. Data is null");
    }

    public String getErrorMessage() {
        if (isSuccess()) {
            return null;
        }
        return errorMessage;
    }
}
