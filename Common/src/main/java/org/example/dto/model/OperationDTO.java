package org.example.dto.model;

import java.io.Serializable;

public record OperationDTO(Integer amount, Long userId) implements Serializable {
}
