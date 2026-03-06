package org.example.exception;

import lombok.Getter;
import org.example.dto.Status;

@Getter
public class BusinessException extends RuntimeException {
    private final Status status;

    public BusinessException(Status status, String message) {
        super(message);
        this.status = status;
    }
}
