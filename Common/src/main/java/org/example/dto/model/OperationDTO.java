package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class OperationDTO implements DTO, Serializable {
    private Long id;
    private Long accountId;
    private String description;
    private BigDecimal amount;
    private Instant dateTime;
    private Long userId;
    private Long categoryId;
}


