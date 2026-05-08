package org.example.domain.model;

import org.example.domain.exception.BadParameterException;

public enum AccountMemberRole {
    READER,
    WRITER,
    MANAGER,
    ;

    public static AccountMemberRole fromString(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new BadParameterException("Cannot get Role with name = " + name);
        }
    }
}
