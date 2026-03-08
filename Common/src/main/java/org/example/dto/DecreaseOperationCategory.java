package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DecreaseOperationCategory {
    FOOD("Еда"),
    TRANSPORT("Транспорт"),
    ENTERTAINMENT("Развлечения"),
    SHOPPING("Покупки"),
    HEALTH("Здоровье"),
    EDUCATION("Образование"),
    UTILITIES("Коммунальные услуги"),
    RENT("Аренда"),
    COMMUNICATION("Связь"),
    INSURANCE("Страховка"),
    TAXES("Налоги"),
    CHARITY("Благотворительность"),
    REPAIRS("Ремонт"),
    CLOTHES("Одежда"),
    SPORT("Спорт"),
    TRAVEL("Путешествия"),
    OTHER("Другое"),
    ;

    private final String name;

    public static DecreaseOperationCategory getByName(String name) {
        if (name == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(category -> category.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }
}
