package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.TransactionUtils;
import org.example.dao.BudgetDAO;
import org.example.dao.RoleDAO;
import org.example.dao.UserDAO;
import org.example.dto.request.AuthRequest;
import org.example.model.Budget;
import org.example.model.Role;
import org.example.model.User;

import java.util.Set;

@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final BudgetDAO budgetDAO;

    public Long signup(AuthRequest request) {
        return TransactionUtils.executeInTransaction(session -> {
            String username = request.getUsername();

            if (userDAO.findByUsername(session, username).isPresent()) {
                throw new RuntimeException("Username already exists " + username);
            }

            Role role = roleDAO.findById(session, 1L)
                    .orElseThrow(() -> new RuntimeException("Role not found with id = 1"));

            User user = new User();
            user.setUsername(username);
            user.setPassword(request.getPassword());
            user.setRoles(Set.of(role));
            session.persist(user);

            Budget budget = new Budget();
            budget.setAmount(0);
            budget.setUser(user);
            session.persist(budget);

            return user.getId();
        });
    }

    public Long signin(AuthRequest request) { // TODO token for auth
        return TransactionUtils.executeInTransaction(session -> {
            String username = request.getUsername();

            User user = userDAO.findByUsername(session, username)
                    .orElseThrow(() -> new RuntimeException("User not found with username = " + username));

            if (user.getPassword().equals(request.getPassword())) {
                return user.getId();
            }

            throw new RuntimeException("Invalid password");
        });
    }
}
