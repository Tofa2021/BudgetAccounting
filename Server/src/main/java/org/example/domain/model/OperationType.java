package org.example.domain.model;

import org.example.domain.exception.BadParameterException;

public enum OperationType {
    EXPENSE,
    INCOME,
    ;

    public static OperationType fromString(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new BadParameterException("Cannot get Role with name = " + name);
        }
    }
}
