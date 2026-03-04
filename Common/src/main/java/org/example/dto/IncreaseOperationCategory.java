package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IncreaseOperationCategory {
    SALARY("Зарплата"),
    ;

    private final String name;
}
