package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.OperationCategory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
public class OperationDTO implements Serializable, DTO {
    private Long id;
    private Long budgetId;
    private Long userId;
    private OperationCategory category;
    private BigDecimal amount;
    private Instant dateTime;
}


