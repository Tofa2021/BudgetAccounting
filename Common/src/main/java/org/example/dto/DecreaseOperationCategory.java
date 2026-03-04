package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DecreaseOperationCategory {
    FOOD("Еда"),
    TRANSPORT("Транспорт"),
    ;

    private final String name;
}
