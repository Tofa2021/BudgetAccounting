package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum IncreaseOperationCategory {
    SALARY("Зарплата"),
    ;

    private final String name;

    public static IncreaseOperationCategory getByName(String name) {
        if (name == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .get();
    }
}
