package org.example.domain.model;

import org.example.domain.exception.BadParameterException;

public enum HouseholdMemberRole {
    OWNER,
    MANAGER,
    MEMBER,
    ;

    public static HouseholdMemberRole fromString(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new BadParameterException("Cannot get Role with name = " + name);
        }
    }
}
