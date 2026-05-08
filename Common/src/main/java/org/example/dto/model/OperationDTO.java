package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
public class OperationDTO implements Serializable, DTO {
    private Long id;
    private Long accountId;
    private String description;
    private BigDecimal amount;
    private Instant dateTime;
    private Long userId;
    private Long categoryId;
}


