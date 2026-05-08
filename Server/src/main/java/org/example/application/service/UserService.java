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
import org.example.dto.request.user.AuthRequest;
import org.example.dto.request.user.LogoutRequest;
import org.example.dto.request.user.UpdateUserRequest;
import org.example.dto.response.Status;
import org.example.infrastructure.security.PasswordEncoder;
import org.example.infrastructure.security.TokenProvider;

@Slf4j
@RequiredArgsConstructor
public class UserService { // TODO check rights and TODO logging
    private final TransactionManager transactionManager;
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Pair<String, String> signUp(AuthRequest request) {
        return transactionManager.executeInTransaction(() -> {
            String username = request.getUsername();

            if (userDAO.findByUsername(username).isPresent()) {
                throw new UserAlreadyExistsExceptionException(username);
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userDAO.save(user);

            Long userId = user.getId();
            log.info("User sighed up with username = {}", username);
            return new Pair<>(tokenProvider.generateAccessToken(userId), tokenProvider.generateRefreshToken(userId));
        });
    }

    public Pair<String, String> signIn(AuthRequest request) {
        return transactionManager.executeInTransaction(() -> {
            String username = request.getUsername();

            User user = userDAO.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(username));
            Long userId = user.getId();

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new Pair<>(tokenProvider.generateAccessToken(userId), tokenProvider.generateRefreshToken(userId));
            }

            log.info("User sign in with username = {}", username);
            throw new BusinessException(Status.UNAUTHORIZED, "Invalid password");
        });
    }

    public User update(UpdateUserRequest request, Long userId) {
        return transactionManager.executeInTransaction(() -> {
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            String username = request.getUsername();
            if (username != null) {
                if (userDAO.findByUsername(username).isPresent()) {
                    throw new UserAlreadyExistsExceptionException(username);
                }
                user.setUsername(request.getUsername());
            }
            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            return userDAO.save(user);
        });
    }

    public void delete(Long userId) {
        transactionManager.executeInTransaction(() -> {
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            userDAO.delete(user);
        });
    }

    public void logout(LogoutRequest request) {
        tokenProvider.invalidateRefreshToken(request.getRefreshToken());
    }

    public Pair<String, String> refreshTokens(String refreshToken) {
        if (!tokenProvider.isValidateRefreshToken(refreshToken)) {
            throw new BusinessException(Status.UNAUTHORIZED, "Invalid refresh token");
        }
        tokenProvider.invalidateRefreshToken(refreshToken);

        Long userId = tokenProvider.getUserIdFromRefreshToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessToken(userId);
        String newRefreshToken = tokenProvider.generateRefreshToken(userId);

        return new Pair<>(newAccessToken, newRefreshToken);
    }

    public User get(ModelIdAuthorizedRequest request) {
        return userDAO.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException(request.getId()));
    }

    public User getMe(Long userId) {
        return userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
