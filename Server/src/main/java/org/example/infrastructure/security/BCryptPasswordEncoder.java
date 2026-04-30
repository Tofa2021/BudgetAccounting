package org.example.infrastructure.security;

import lombok.AllArgsConstructor;
import org.example.domain.exception.BadParameterException;
import org.mindrot.jbcrypt.BCrypt;

@AllArgsConstructor
public class BCryptPasswordEncoder implements PasswordEncoder {
    private final int strength;

    @Override
    public String encode(String rawPassword) {
        validatePassword(rawPassword);
        String salt = BCrypt.gensalt(strength);
        return BCrypt.hashpw(rawPassword, salt);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(rawPassword, encodedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new BadParameterException("Password is bad");
        }
    }
}
