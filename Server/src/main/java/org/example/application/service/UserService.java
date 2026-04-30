package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.TransactionManager;
import org.example.domain.dao.BudgetDAO;
import org.example.domain.dao.RoleDAO;
import org.example.domain.dao.UserDAO;
import org.example.domain.exception.BusinessException;
import org.example.domain.exception.RoleNotFoundException;
import org.example.domain.exception.UserAlreadyExistsException;
import org.example.domain.exception.UserNotFoundException;
import org.example.domain.model.Budget;
import org.example.domain.model.Role;
import org.example.domain.model.User;
import org.example.dto.Pair;
import org.example.dto.Status;
import org.example.dto.request.AuthRequest;
import org.example.infrastructure.security.PasswordEncoder;
import org.example.infrastructure.security.TokenProvider;

import java.util.Set;

@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final BudgetDAO budgetDAO;
    private final TransactionManager transactionManager;

    public Pair<String, String> signUp(AuthRequest request) {
        return transactionManager.executeInTransaction(() -> {
            String username = request.getUsername();

            if (userDAO.findByUsername(username).isPresent()) {
                throw new UserAlreadyExistsException(username);
            }

            Role role = roleDAO.findById(1L)
                    .orElseThrow(() -> new RoleNotFoundException(1L)); // TODO replace id=1L on Real role

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(Set.of(role));
            userDAO.save(user);

            Budget budget = new Budget();
            budget.setAmount(0.);
            budget.setUser(user);
            budgetDAO.save(budget);

            Long userId = user.getId();
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

            throw new BusinessException(Status.UNAUTHORIZED, "Invalid password");
        });
    }
}
