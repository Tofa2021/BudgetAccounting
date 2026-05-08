package org.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    BYN("BYN", "Белорусский рубль"),
    ;

    private final String code;
    private final String name;
}
