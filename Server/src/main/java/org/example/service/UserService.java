package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.RoleDAO;
import org.example.dao.UserDAO;
import org.example.dto.Pair;
import org.example.dto.Status;
import org.example.dto.request.AuthRequest;
import org.example.exception.BusinessException;
import org.example.exception.RoleNotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.example.model.Budget;
import org.example.model.Role;
import org.example.model.User;
import org.example.security.JwtProvider;
import org.example.security.PasswordEncoder;
import org.example.util.TransactionUtils;

import java.util.Set;

@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;

    public Pair<String, String> signup(AuthRequest request) {
        return TransactionUtils.executeInTransaction(session -> {
            String username = request.getUsername();

            if (userDAO.findByUsername(session, username).isPresent()) {
                throw new UserAlreadyExistsException(username);
            }

            Role role = roleDAO.findById(session, 1L)
                    .orElseThrow(() -> new RoleNotFoundException(1L)); // TODO replace 1L on smth

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(Set.of(role));
            session.persist(user);

            Budget budget = new Budget();
            budget.setAmount(0.);
            budget.setUser(user);
            session.persist(budget);

            Long userId = user.getId();
            return new Pair<>(jwtProvider.generateAccessToken(userId), jwtProvider.generateRefreshToken(userId));
        });
    }

    public Pair<String, String> signin(AuthRequest request) {
        return TransactionUtils.executeInTransaction(session -> {
            String username = request.getUsername();

            User user = userDAO.findByUsername(session, username)
                    .orElseThrow(() -> new UserNotFoundException(username));
            Long userId = user.getId();

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new Pair<>(jwtProvider.generateAccessToken(userId), jwtProvider.generateRefreshToken(userId));
            }

            throw new BusinessException(Status.SERVER_ERROR, "Invalid password");
        });
    }
}
