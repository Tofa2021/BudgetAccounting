package org.example.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.dto.Status;

@Getter
@AllArgsConstructor
public class Result<T> {
    private Status status;
    @Getter(AccessLevel.NONE)
    private T data;
    private String errorMessage;

    public static <T> Result<T> success(T data) {
        return new Result<>(Status.OK, data, "");
    }

    public static <T> Result<T> error(Status status, String errorMessage) {
        return new Result<>(status, null, errorMessage);
    }

    public boolean isSuccess() {
        return status == Status.OK;
    }

    public T getData() {
        if (isSuccess()) {
            return data;
        }
        throw new IllegalStateException("Data is null");
    }
}
