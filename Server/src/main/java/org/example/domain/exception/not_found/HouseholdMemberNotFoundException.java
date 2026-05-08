package org.example.domain.exception.not_found;

public class HouseholdMemberNotFoundException extends NotFoundException {
    public HouseholdMemberNotFoundException(Long id) {
        super("HouseholdMember", "id", String.valueOf(id));
    }

    public HouseholdMemberNotFoundException(Long userId, Long householdId) {
        super("HouseholdMember with userId = " + userId + " and householdId = " + householdId + " not found");
    }
}
