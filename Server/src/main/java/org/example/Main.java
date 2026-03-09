package org.example;

import org.example.connection.ConnectionManager;
import org.example.dao.BudgetDAO;
import org.example.dao.OperationDAO;
import org.example.dao.RoleDAO;
import org.example.dao.UserDAO;
import org.example.security.BCryptPasswordEncoder;
import org.example.security.JwtProvider;
import org.example.security.PasswordEncoder;
import org.example.service.BudgetService;
import org.example.service.OperationService;
import org.example.service.UserService;
import org.example.util.Hibernate;

public class Main {
    public static void main(String[] args) {
        ConnectionManager connectionManager = new ConnectionManager();

        BudgetDAO budgetDAO = new BudgetDAO();
        RoleDAO roleDAO = new RoleDAO();
        UserDAO userDAO = new UserDAO();
        OperationDAO operationDAO = new OperationDAO();

        JwtProvider jwtProvider = new JwtProvider();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        BudgetService budgetService = new BudgetService(budgetDAO, userDAO);
        UserService userService = new UserService(passwordEncoder, jwtProvider, userDAO, roleDAO);
        OperationService operationService = new OperationService(operationDAO, budgetDAO);

        Controller controller = new Controller(jwtProvider, budgetService, userService, operationService);
        Hibernate.getSessionFactory();

        new Thread(() -> connectionManager.start(controller)).start();
    }
}
