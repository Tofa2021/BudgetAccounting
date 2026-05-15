package org.example.domain.exception.already_exists;

public class HouseholdMemberAlreadyExistsException extends AlreadyExistsException {
    public HouseholdMemberAlreadyExistsException(Long id) {
        super("HouseholdMember", "id", String.valueOf(id));
    }

    public HouseholdMemberAlreadyExistsException(Long userId, Long householdId) {
        super("User with id = " + userId +
                " is already a member of household with id = " + householdId);
    }
}
