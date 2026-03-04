package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.HibernateUtils;
import org.example.dao.BudgetDAO;
import org.example.dao.RoleDAO;
import org.example.dao.UserDAO;
import org.example.dto.request.AuthRequest;
import org.example.model.Budget;
import org.example.model.Role;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Set;

@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final BudgetDAO budgetDAO;

    public Long create(AuthRequest request) {
        String username = request.getUsername();

        if (userDAO.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists " + username);
        }

        Role role = roleDAO.findById(1L)
                .orElseThrow(() -> new RuntimeException("Role not found with id = 1"));

        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User user = new User();
            user.setUsername(username);
            user.setPassword(request.getPassword());
            user.setRoles(Set.of(role));

            session.persist(user);

            Budget budget = new Budget();
            budget.setAmount(0);
            budget.setUser(user);

            session.persist(budget);

            transaction.commit();
            return user.getId();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public Long signin(AuthRequest request) { // TODO token for auth
        String username = request.getUsername();

        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username = " + username));

        if (user.getPassword().equals(request.getPassword())) {
            return user.getId();
        }

        throw new RuntimeException("Invalid password");
    }
}
