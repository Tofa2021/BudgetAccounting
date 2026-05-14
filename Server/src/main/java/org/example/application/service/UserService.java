package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Pair;
import org.example.domain.dao.PersistenceManager;
import org.example.domain.dao.UserDAO;
import org.example.domain.exception.BusinessException;
import org.example.domain.exception.already_exists.UserAlreadyExistsExceptionException;
import org.example.domain.exception.not_found.UserNotFoundException;
import org.example.domain.model.User;
import org.example.infrastructure.security.PasswordEncoder;
import org.example.infrastructure.security.TokenProvider;
import org.example.response.Status;

@Slf4j
@RequiredArgsConstructor
public class UserService { // TODO check rights
    private final PersistenceManager persistenceManager;
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Pair<String, String> signUp(String username, String password) {
        log.debug("Signing up with username = {}", username);

        return persistenceManager.executeTransaction(() -> {
            if (userDAO.findByUsername(username).isPresent()) {
                throw new UserAlreadyExistsExceptionException(username);
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            userDAO.save(user);

            Long userId = user.getId();
            log.info("User signed up with username = {}", username);
            return new Pair<>(tokenProvider.generateAccessToken(userId), tokenProvider.generateRefreshToken(userId));
        });
    }

    public Pair<String, String> signIn(String username, String password) {
        log.debug("Signing in with username = {}", username);

        return persistenceManager.executeReadOnly(() -> {
            User user = userDAO.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(username));
            Long userId = user.getId();

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BusinessException(Status.UNAUTHORIZED, "Invalid password");
            }

            log.info("User signed in with username = {}", username);
            return new Pair<>(tokenProvider.generateAccessToken(userId), tokenProvider.generateRefreshToken(userId));
        });
    }

    public void update(String username, String password, Long userId) {
        log.debug("Updating user with id = {}", userId);

        persistenceManager.executeTransaction(() -> {
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            if (username != null) {
                if (userDAO.findByUsername(username).isPresent()) {
                    throw new UserAlreadyExistsExceptionException(username);
                }
                user.setUsername(username);
            }
            if (password != null) {
                user.setPassword(passwordEncoder.encode(password));
            }

            userDAO.save(user);
            log.info("User with id = {} updated. New username = {}", userId, username);
        });
    }

    public void delete(Long userId) {
        log.debug("Deleting user with id = {}", userId);

        persistenceManager.executeTransaction(() -> {
            userDAO.deleteById(userId);
            log.info("User deleted with id = {}", userId);
        });
    }

    public void logout(String accessToken, String refreshToken) { // TODO is it need to check if userId from refresh and access tokens match
        log.debug("Logging out");

        Long userId = tokenProvider.getUserIdFromRefreshToken(refreshToken);
        tokenProvider.invalidateAccessToken(accessToken);
        tokenProvider.invalidateRefreshToken(refreshToken);
        log.info("User logged out with id = {}", userId);
    }

    public Pair<String, String> refreshTokens(String refreshToken) {
        log.debug("Refreshing tokens");

        if (!tokenProvider.isValidateRefreshToken(refreshToken)) {
            throw new BusinessException(Status.UNAUTHORIZED, "Invalid refresh token");
        }
        tokenProvider.invalidateRefreshToken(refreshToken);

        Long userId = tokenProvider.getUserIdFromRefreshToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessToken(userId);
        String newRefreshToken = tokenProvider.generateRefreshToken(userId);

        log.info("User with id = {} refreshed tokens", userId);
        return new Pair<>(newAccessToken, newRefreshToken);
    }

    public User get(Long id) {
        log.debug("Getting user with id = {}", id);

        return persistenceManager.executeReadOnly(() -> {
            User user = userDAO.findByIdWithMembers(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
            log.info("User gotten with id = {}", id);
            return user;
        });
    }
}
