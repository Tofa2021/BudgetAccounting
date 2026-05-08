package org.example.domain.exception.already_exists;

public class HouseholdMemberAlreadyExistsExceptionException extends AlreadyExistsException {
    public HouseholdMemberAlreadyExistsExceptionException(Long id) {
        super("HouseholdMember", "id", String.valueOf(id));
    }

    public HouseholdMemberAlreadyExistsExceptionException(Long userId, Long householdId) {
        super("User with id = " + userId +
                " is already a member of household with id = " + householdId);
    }
}
