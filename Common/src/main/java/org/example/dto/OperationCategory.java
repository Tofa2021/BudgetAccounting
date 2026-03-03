package org.example.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OperationCategory {
    FOOD("Еда"),
    TRANSPORT("Транспорт"),
    ;

    private final String name;
}
