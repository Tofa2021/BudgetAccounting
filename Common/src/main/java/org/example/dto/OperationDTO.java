package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.enums.Currency;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class OperationDTO implements DTO, Serializable {
    private Long id;
    private Long accountId;
    private String description;
    private BigDecimal amount;
    private Instant dateTime;
    private Long accountMemberId;
    private Long categoryId;
    private String categoryName;
    private String type;
    private Currency currency;
}


