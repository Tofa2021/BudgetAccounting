package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Pair;
import org.example.domain.TransactionManager;
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
    private final TransactionManager transactionManager;
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Pair<String, String> signUp(String username, String password) {
        return transactionManager.executeInTransaction(() -> {
            if (userDAO.findByUsername(username).isPresent()) {
                throw new UserAlreadyExistsExceptionException(username);
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            userDAO.save(user);

            Long userId = user.getId();
            log.info("User sighed up with username = {}", username);
            return new Pair<>(tokenProvider.generateAccessToken(userId), tokenProvider.generateRefreshToken(userId));
        });
    }

    public Pair<String, String> signIn(String username, String password) {
        return transactionManager.executeInTransaction(() -> {
            User user = userDAO.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(username));
            Long userId = user.getId();

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BusinessException(Status.UNAUTHORIZED, "Invalid password");
            }

            log.info("User sign in with username = {}", username);
            return new Pair<>(tokenProvider.generateAccessToken(userId), tokenProvider.generateRefreshToken(userId));
        });
    }

    public User update(String username, String password, Long userId) {
        return transactionManager.executeInTransaction(() -> {
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
            log.info("User with id = {} updated. New username = {}", userId, username != null);
            return user;
        });
    }

    public void delete(Long userId) {
        transactionManager.executeInTransaction(() -> {
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            log.info("User with id = {} deleted", userId);
            userDAO.delete(user);
        });
    }

    public void logout(String accessToken, String refreshToken) {
        Long userId = tokenProvider.getUserIdFromRefreshToken(refreshToken);
        tokenProvider.invalidateRefreshToken(refreshToken);
        log.info("User with id = {} logged out", userId);
    }

    public Pair<String, String> refreshTokens(String refreshToken) {
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
        return userDAO.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getMe(Long userId) {
        return userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
