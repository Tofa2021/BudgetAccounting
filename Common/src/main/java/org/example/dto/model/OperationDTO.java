package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public abstract class OperationDTO implements Serializable {
    private final Integer amount;
    private final Long userId;
}
