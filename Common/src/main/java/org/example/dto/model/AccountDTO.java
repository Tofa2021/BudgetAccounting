package org.example.dto.model;

import lombok.Getter;
import lombok.Setter;
import org.example.enums.Currency;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AccountDTO implements DTO {
    private Long id;
    private String name;
    private Currency currency;
    private BigDecimal amount;
    private List<Long> memberIds;
    private Long householdId;
}
