package org.example.domain.exception.not_found;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(Long id) {
        super("Role", "id", String.valueOf(id));
    }
}
