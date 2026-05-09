package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.enums.Currency;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class AccountDTO implements DTO, Serializable {
    private Long id;
    private String name;
    private Currency currency;
    private BigDecimal amount;
    private List<Long> memberIds;
    private Long householdId;
}
