package org.example.domain.exception.not_found;

public class HouseholdNotFoundException extends NotFoundException {
    public HouseholdNotFoundException(Long id) {
        super("Household", "id", String.valueOf(id));
    }
}
