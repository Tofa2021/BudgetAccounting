package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum IncreaseOperationCategory {
    SALARY("Зарплата"),
    BONUS("Премия"),
    GIFT("Подарок"),
    INVESTMENT("Инвестиции"),
    FREELANCE("Фриланс"),
    BUSINESS("Бизнес"),
    RENTAL("Аренда"),
    REFUND("Возврат"),
    OTHER("Другое"),
    ;

    private final String name;

    public static IncreaseOperationCategory getByName(String name) {
        if (name == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }
}
