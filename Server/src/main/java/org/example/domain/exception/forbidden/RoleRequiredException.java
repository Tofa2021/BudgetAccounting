package org.example.domain.exception.forbidden;

import org.example.domain.model.AccountMemberRole;
import org.example.domain.model.HouseholdMemberRole;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RoleRequiredException extends ForbiddenException {
    public RoleRequiredException(HouseholdMemberRole actualRole, HouseholdMemberRole... requiredRoles) {
        String requiredRolesString = Arrays.stream(requiredRoles)
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        String roleWord = requiredRoles.length == 1 ? "role" : "roles";
        super("Required household " + roleWord + " = " + requiredRolesString + " but actual role = " + actualRole);
    }

    public RoleRequiredException(AccountMemberRole actualRole, AccountMemberRole... requiredRoles) {
        String requiredRolesString = Arrays.stream(requiredRoles)
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        String roleWord = requiredRoles.length == 1 ? "role" : "roles";
        super("Required account " + roleWord + " = " + requiredRolesString + " but actual role = " + actualRole);
    }

    public RoleRequiredException(String message) {
        super(message);
    }
}
