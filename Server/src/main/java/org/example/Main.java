package org.example;

import org.example.connection.ConnectionManager;
import org.example.connection.RequestProcessor;
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
import org.example.util.HibernateSessionManager;
import org.example.util.SessionManager;

public class Main {
    public static void main(String[] args) {
        BudgetDAO budgetDAO = new BudgetDAO();
        RoleDAO roleDAO = new RoleDAO();
        UserDAO userDAO = new UserDAO();
        OperationDAO operationDAO = new OperationDAO();

        JwtProvider jwtProvider = new JwtProvider();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        SessionManager sessionManager = new HibernateSessionManager();

        BudgetService budgetService = new BudgetService(budgetDAO, userDAO, sessionManager);
        UserService userService = new UserService(passwordEncoder, jwtProvider, userDAO, roleDAO, sessionManager);
        OperationService operationService = new OperationService(operationDAO, budgetDAO, sessionManager);

        RequestProcessor requestProcessor = new RequestProcessor(jwtProvider, budgetService, userService, operationService);

        ConnectionManager connectionManager = new ConnectionManager(requestProcessor);

        connectionManager.start();
    }
}
